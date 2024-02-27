package com.smartpayments.superpos.aggregationservice.resources

import com.smartpayments.superpos.aggregationservice.business.TaskService
import com.smartpayments.superpos.aggregationservice.resources.dto.AggregationServiceTaskDTO
import com.smartpayments.superpos.aggregationservice.resources.dto.BillingRequestDTO
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import java.util.concurrent.Callable
import java.util.concurrent.CompletableFuture


@RestController
@RequestMapping("/aggregation-service/transaction")
class TransactionController(
    private val taskService: TaskService) {
    private val logger = KotlinLogging.logger {}

    @PostMapping("/billing-transaction", produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseBody
    fun getBillingTransactions(@RequestBody billingRequestDTOs: List<BillingRequestDTO>): CompletableFuture<AggregationServiceTaskDTO> {
        logger.info { "Request received: ${billingRequestDTOs}" }
        val task = taskService.createBillingTransactionTask(billingRequestDTOs)
        return CompletableFuture.completedFuture(AggregationServiceTaskDTO(task.id?:0,task.status.ordinal,task.type, task.error, task.customProps))
    }

    @GetMapping(value = ["/testCallable"])
    fun echoHelloWorld(): Callable<String> {
        return Callable {
            Thread.sleep(5000)
            logger.info { "Hello!" }
            "Hello World !!"
        }
    }
}
