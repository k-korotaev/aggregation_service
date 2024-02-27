package com.smartpayments.superpos.aggregationservice.business

import com.fasterxml.jackson.databind.ObjectMapper
import com.smartpayments.superpos.aggregationservice.business.exceptions.BadRequestParamsException
import com.smartpayments.superpos.aggregationservice.repository.jsonb.AccountRepository
import com.smartpayments.superpos.aggregationservice.repository.jsonb.GateRepository
import com.smartpayments.superpos.aggregationservice.repository.jsonb.TaskRepository
import com.smartpayments.superpos.aggregationservice.repository.jsonb.TransactionRepository
import com.smartpayments.superpos.aggregationservice.repository.jsonb.entity.AccountEntity
import com.smartpayments.superpos.aggregationservice.repository.jsonb.entity.GateEntity
import com.smartpayments.superpos.aggregationservice.repository.jsonb.entity.TaskEntity
import com.smartpayments.superpos.aggregationservice.repository.jsonb.entity.TaskEntity.*
import com.smartpayments.superpos.aggregationservice.repository.jsonb.entity.TransactionEntity
import com.smartpayments.superpos.aggregationservice.resources.dto.BillingRequestDTO
import com.smartpayments.superpos.aggregationservice.resources.dto.BillingTransactionDTO
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.core.task.TaskExecutor
import org.springframework.orm.ObjectOptimisticLockingFailureException
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime


@Service
class TaskService(
    private val taskRepository: TaskRepository,
    private val storageService: StorageService,
    private val accountRepository: AccountRepository,
    @Qualifier("applicationTaskExecutor")
    private val taskExecutor: TaskExecutor,
    private val transactionRepository: TransactionRepository,
    private val gateRepository: GateRepository,
    private val objectMapper: ObjectMapper
) {

    private val logger = KotlinLogging.logger {}

    fun get(taskId: Long): TaskEntity? {
        return taskRepository.getTaskById(taskId)
    }

    fun create(
        type: EAggregationServiceTaskType,
        request: List<BillingRequestDTO>,
        status: TaskStatus = TaskStatus.CREATED,
        createdAt: LocalDateTime = LocalDateTime.now(),
        customProps: Map<String, Any>? = null
    ): TaskEntity {
        return taskRepository.saveAndFlush(
            TaskEntity(
                type = type,
                status = status,
                taskParams = request,
                createdAt = createdAt,
                customProps = customProps
            )
        )
    }


    fun createBillingTransactionTask(request: List<BillingRequestDTO>): TaskEntity {
        logger.info { "creating billing tx task: ${request}" }
        val json = objectMapper.writeValueAsString(request)
        val exists = this.taskRepository.getLastTaskByTypeWithParams(json, EAggregationServiceTaskType.TransactionsBetweenTxFile)
        var task =
            if (exists != null) {
                logger.info { "Task ${exists.id} already exists status: ${exists.status}" }
                exists
            }
            else
                this.create(EAggregationServiceTaskType.TransactionsBetweenTxFile, request)
        task = taskRepository.getTaskById(task.id!!)!!
        return task
    }

    @Transactional
    fun updateTaskStatus(id: Long, status: TaskStatus, error: String?=null, props: Map<String, Any>?=null):TaskEntity? {
        val task = taskRepository.getTaskById(id)
        task?.status = status
        task?.error = error
        task?.customProps = props
        return taskRepository.saveAndFlush(task!!)
    }

    fun getBillingTransaction(filter: BillingRequestDTO, task: TaskEntity): List<TransactionEntity> {
        val accountCount = accountRepository.getAccountsCountByMerchantIdAndAccountId(filter.merchantId, filter.accountId!!)
        logger.info { "Task ${task.id}. There are ${accountCount} accounts with filter ${filter}. Getting accounts ..." }
        val accounts = accountRepository.getAccountsByMerchantIdAndAccountId(filter.merchantId, filter.accountId)
        val accountIds = accounts.map(AccountEntity::id).toMutableList()
        logger.info { "Task ${task.id}. Accounts got: ${accountIds}" }

        val startTransaction = transactionRepository.getTransactionEntityById(filter.startTransactionId)
        val endTransaction = transactionRepository.getTransactionEntityById(filter.endTransactionId)

        if (startTransaction==null || endTransaction==null){
            throw BadRequestParamsException("Invalid transaction filter params")
        }

        val gates = gateRepository.getGatesByBankId(filter.bankId!!)
        val gateIds = gates.map(GateEntity::id).toList()

        val transactionCount = transactionRepository.getTransactionCountByBillingAccountId(
            accountIds,
            type = filter.type!!,
            startDate = startTransaction.data.endDate!!,
            endDate = endTransaction.data.endDate!!,
            gateIds = gateIds
        )
        logger.info { "Task ${task.id}. There are ${transactionCount} transaction with filter ${filter}. Let's get this transactions ..." }
        return transactionRepository.getTransactionListByBillingAccountId(
            accountIds,
            type = filter.type,
            startDate = startTransaction.data.endDate!!,
            endDate = endTransaction.data.endDate!!,
            gateIds = gateIds
        )
    }



    @Scheduled(fixedDelay = 6000)
    @Transactional
    fun processTasks() {
        logger.info { "check new task" }
        val task: TaskEntity? = taskRepository.findFirstByStatusOrderByCreatedAtAsc(TaskStatus.CREATED, EAggregationServiceTaskType.TransactionsBetweenTxFile)
        if (task!=null) {
            // get task to process
            updateTaskStatus(task.id!!, TaskStatus.IN_PROGRESS)
            logger.info { "Task ${task.id}.${task.version}. Run asynchronous execution." }
            taskExecutor.execute { executeTaskAsync(task) }
        }
    }


    @Transactional(readOnly = true)
    fun executeTaskAsync(task: TaskEntity) {
        try {
            val result = mutableListOf<BillingTransactionDTO>()
            val transactions = mutableListOf<TransactionEntity>()
            for ((index, filter) in task.taskParams.withIndex()) {
                logger.info { "Task ${task.id}. Getting transactions with filter ${index + 1}.${filter}. " }
                transactions.addAll(this.getBillingTransaction(filter, task))
                logger.info { "Task ${task.id}. Transactions got with filter ${index + 1}.${filter}." }
            }
            logger.info { "Task ${task.id}. Got ${transactions.count()} transactions. Transform to response." }

            result.addAll(transactions.map {
                BillingTransactionMapper.toDTO(
                    it,
                    accountRepository.getAccountById(it.data.billingAccountId!!)
                )
            })

            val uploadResult = storageService.uploadObjects(task, result)
            logger.info { "Task ${task.id}. Result uploaded to storage." }
            // approve task
            updateTaskStatus(task.id!!, TaskStatus.COMPLETE, props = hashMapOf(Pair("uploadResult", uploadResult)))
            logger.info { "Task ${task.id} completed successfully." }
        } catch (e: InterruptedException) {
            logger.error { "InterruptedException occurred during task ${task.id} executing: ${e.message}:${e.stackTrace}" }
            throw RuntimeException(e)
        }
        catch (optimistic: ObjectOptimisticLockingFailureException){
            val changedTask = taskRepository.getTaskById(task.id!!)!!
            logger.error { "Task ${task.id}.${task.version}$:{task.status}. Status has been changed status in another transaction from ${changedTask.status}:${changedTask.version}." }
            throw RuntimeException(optimistic)
        } catch (e: Exception) {
            logger.error { "Task ${task.id}. Exception occurred during executing: ${e.message}:${e.stackTrace}" }
            // decline task
            updateTaskStatus(task.id!!, TaskStatus.ERROR, e.message, e.stackTrace.map{Pair(it.className, it.methodName)}.toMap())
            logger.info { "task ${task.id} has been finished with error ${e.message}" }
            throw RuntimeException(e)
            // todo: retry for task
        }
    }
}