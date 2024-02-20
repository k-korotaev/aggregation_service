package com.smartpayments.superpos.aggregationservice.business

import com.smartpayments.superpos.aggregationservice.business.exceptions.BadRequestParamsException
import com.smartpayments.superpos.aggregationservice.repository.jsonb.AccountRepository
import com.smartpayments.superpos.aggregationservice.repository.jsonb.GateRepository
import com.smartpayments.superpos.aggregationservice.repository.jsonb.TransactionRepository
import com.smartpayments.superpos.aggregationservice.repository.jsonb.entity.AccountEntity
import com.smartpayments.superpos.aggregationservice.repository.jsonb.entity.GateEntity
import com.smartpayments.superpos.aggregationservice.repository.jsonb.entity.TaskEntity
import com.smartpayments.superpos.aggregationservice.repository.jsonb.entity.TransactionEntity
import com.smartpayments.superpos.aggregationservice.resources.dto.BillingRequestDTO
import com.smartpayments.superpos.aggregationservice.resources.dto.BillingTransactionDTO
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service

@Service
class BillingTransactionService (
    private val transactionRepository: TransactionRepository,
    private val accountRepository: AccountRepository,
    private val gateRepository: GateRepository,
    private val taskService: TaskService,
    private val storageService: StorageService) {

    private val logger = KotlinLogging.logger {}

    fun getBillingTransaction(request: List<BillingRequestDTO>): List<BillingTransactionDTO> {
        logger.info { "creating billing tx task: ${request}" }
        val result = mutableListOf<BillingTransactionDTO>()
        val task = taskService.create(TaskEntity.EAggregationServiceTaskType.TransactionsBetweenTxFile)
        try {

            val transactions = mutableListOf<TransactionEntity>()
            for ((index, filter) in request.withIndex()) {
                logger.info { "Task ${task.id}. Getting transactions with filter ${index + 1}.${filter}. "}
                transactions.addAll(getBillingTransaction(filter, task))
                logger.info { "Task ${task.id}. Transactions got with filter ${index + 1}.${filter}." }
            }
            logger.info { "Task ${task.id}. Got ${transactions.count()} transactions. Transform to response." }

            result.addAll(transactions.map {
                BillingTransactionMapper.toDTO(
                    it,
                    accountRepository.getAccountById(it.data.billingAccountId!!)
                )
            })

            /*
                todo: to store in S3 and return result to objectSize
            */

            val uploadResult = storageService.uploadObjects(task, result)

            logger.info { "Task ${task.id}. Result uploaded to storage." }

            taskService.approve(
                task,
                hashMapOf(Pair("uploadResult", uploadResult))
            )
        }
        catch (ex: Exception){
            logger.error { "Failed to process task ${task.id}: ${ex.message}"}
            task.customProps= hashMapOf(Pair("trace", ex.stackTraceToString()))
            taskService.decline(task, ex.message.toString())
            throw BadRequestParamsException("Failed to process task ${task.id}: ${ex.message}", ex)
        }

        logger.info { "Task ${task.id} finished." }
        return result
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
}