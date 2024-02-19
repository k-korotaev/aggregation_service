package com.smartpayments.superpos.aggregationservice.resources.dto

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDateTime

data class BillingTransactionDTO(
    @JsonProperty("_id")
    val id: String?,
    val type: String?,
    val merchantOrderId: String?,
    val endDate: LocalDateTime?,
    val amount: Int?,
    val currency: String?,
    val rrn: String?,
    @JsonProperty("Receiver")
    val receiver: Receiver? = null,
    val authorizationCode: String?,
    val commissionAmount: Int?,
    val commissionCurrency: String?,
    @JsonProperty("customer")
    val customer: Customer? = null,
    @JsonProperty("card")
    val card: Card? = null,
    @JsonProperty("processingData")
    val processingData: ProcessingData? = null,
    @JsonProperty("accrualAmount")
    val accrualAmount: AccrualAmount? = null
){

    data class Receiver(
        val name: String?
    )

    data class Customer(
        val firstName: String?,
        val lastName: String?,
        val address: String?,
        val city: String?,
        val zipCode: String?,
        val country: String?,
        val phone: String?,
        val email: String?,
        val ip: String?,
    )

    data class Card(
        val bin: String?,
        val iso: String?,
        val country: String?,
        val type: String?,
    )

    data class Amount(
        val amount: Int?,
        val currency: String?
    )

    data class AccountConditions(
        val fee: Int?
    )

    data class Commission(
        val amount: Int?,
        val currency: String?,
        val accountConditions: AccountConditions?
    )

    data class ProcessingData(
        val amount: Amount?,
        val commission: Commission?
    )

    data class AccrualAmount(
        val amount: Int?,
        val currency: String?
    )
}
