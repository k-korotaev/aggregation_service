package com.smartpayments.superpos.aggregationservice.resources

import com.smartpayments.superpos.aggregationservice.business.BillingTransactionService
import com.smartpayments.superpos.aggregationservice.resources.dto.BillingRequestDTO
import com.smartpayments.superpos.aggregationservice.resources.dto.BillingTransactionDTO
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/aggregation-service/transaction")
class TransactionController(
    private val billingTransactionService: BillingTransactionService) {
    private val logger = KotlinLogging.logger {}

    @PostMapping("/billing-transaction", produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseBody
    fun getBillingTransactions(@RequestBody billingRequestDTOs: List<BillingRequestDTO>): List<BillingTransactionDTO> {
        logger.info { "Request received: ${billingRequestDTOs}" }
        return billingTransactionService.getBillingTransaction(billingRequestDTOs)
    }
}
