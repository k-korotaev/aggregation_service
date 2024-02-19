package com.smartpayments.superpos.aggregationservice.repository.jsonb.entity

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import java.time.LocalDateTime

@Entity(name = "transactions_dump")
@JsonIgnoreProperties(ignoreUnknown = true)
open class TransactionEntity(
    @Id
    open val id : String,
    @Column(name = "document", columnDefinition = "json")
    @JdbcTypeCode(SqlTypes.JSON)
    open val data: Transaction
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    data class Transaction(
        @JsonProperty("_id")
        var id: String?,

        @JsonProperty("Receiver")
        var receiverId: String?,

        @JsonProperty("BillingAccount")
        var billingAccountId: String?,

        var orderId: String?,

        var processingOrderId: String,

        @JsonProperty("Gate")
        var gateId: String?,

        var startDate: LocalDateTime?,

        var endDate: LocalDateTime?,

        var type: String?,

        var status: Int?,

        var merchantOrderId: String?,

        var transactionNumber: Int?,

        var amount: Amount,

        var finalAmount: Amount?,

        var commission: Commission?,

        var processingData: ProcessingData?,

        var system: System?,

        var errorReason: String?,

        var customerData: CustomerData?,

        var customerFingerprint: String? = null
    )

    @JsonIgnoreProperties(ignoreUnknown = true)
    data class Amount(
        var amount: Int?,
        var currency: String?
    )

    @JsonIgnoreProperties(ignoreUnknown = true)
    data class AccountConditions(
        var percent: Int?,
        var min: Int?,
        var max: Int?,
        var fee: Int?
    )

    @JsonIgnoreProperties(ignoreUnknown = true)
    data class Commission(
        var bank: Int?,
        var system: Int?,
        var account: Int?,
        var accountConditions: AccountConditions?,
        var total: Int?,
        var currency: String?,
    )

    @JsonIgnoreProperties(ignoreUnknown = true)
    data class ChargebackData(
        var isReceived: Boolean?
    )

    @JsonIgnoreProperties(ignoreUnknown = true)
    data class ProcessingData(
        var rrn: String?,
        var authorizationCode: String?,
        var amount: Amount?,
        var commission: Commission?,
        var accrualAmount: Amount?,
        var chargebackData: ChargebackData?
    )

    @JsonIgnoreProperties(ignoreUnknown = true)
    data class Card(
        var bin: String?,
        var number: String?,
        var iso: String?,
        var country: String?,
        var type: String?
    )

    @JsonIgnoreProperties(ignoreUnknown = true)
    data class SystemInfo(
        var firstName: String?,
        var lastName: String?,
        var address: String?,
        var city: String?,
        var zipCode: String?,
        var country: String?,
        var phone: String?,
        var email: String?,
        var ip: String?,
        var card: Card?,
        var successUrl: String?,
        var failUrl: String?,
        var finalDomainRequest: Boolean?,
        var finalSuccessUrlDomain: String?,
        var finalFailUrlDomain: String? = null
    )

    @JsonIgnoreProperties(ignoreUnknown = true)
    data class System(
        var info: SystemInfo? = null
    )

    @JsonIgnoreProperties(ignoreUnknown = true)
    data class CustomerData(
        @field:Column(name = "isFeedbackSent")
        var isFeedbackSent: Boolean,
        @field:Column(name = "feedbackInput")
        var feedbackInput: String? = null
    )

    override fun toString(): String {
        return "TransactionEntity(id=$id, gate='${data.gateId}', type='${data.type}', merchant='${data.merchantOrderId}')"
    }
}