package com.smartpayments.superpos.aggregationservice.repository.jsonb.entity

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes

@Entity(name="accounts")
open class AccountEntity (
    @Id
    open val id: String,
    @Column(name = "document", columnDefinition = "json")
    @JdbcTypeCode(SqlTypes.JSON)
    open val data: Account
){
    @JsonIgnoreProperties(ignoreUnknown = true)
    data class Account(
        @JsonProperty("_id")
        var id: String?,

        var name: String?,

        var description: String?,

        var accountType: String?,

        var verifyStatus: EMerchantVerifyStatus?,

        var isDefault: Boolean? = false,

        var balance: Amount?,

        var holdBalance: Amount?,

        var rollingBalance: Amount?,

        @JsonProperty("Gate")
        var gate: String?,

        @JsonProperty("GateBalancer")
        var gateBalancer: String?,

        @JsonProperty("Merchant")
        var merchant: String?,

        var rolling: AccountRolling?,

        var commission: AccountCommission?,

        var actions: AccountActions?,

        var transactionDailyLimit: Int?,

        var transactionMonthlyLimit: Int?,

        var merchantTrafficType: String?
    )

    @JsonIgnoreProperties(ignoreUnknown = true)
    data class Amount(
        var amount: Int?,
        var currency: String?
    )

    @JsonIgnoreProperties(ignoreUnknown = true)
    data class AccountRolling(
        var percent: Int?,
        var holdDays: Int?
    )

    @JsonIgnoreProperties(ignoreUnknown = true)
    data class AccountCommission(
        var percent: Int?,
        var min: Int?,
        var max: Int?,
        var fee: Int?
    )

    @JsonIgnoreProperties(ignoreUnknown = true)
    data class AccountActions(
        var payment: Boolean?,
        var paymentHostToHost: Boolean?,
        var paymentRecurrent: Boolean?,
        var withdrawal: Boolean?,
        var refund: Boolean?
    )

    enum class EMerchantVerifyStatus(val value:Int) {
        NOT_VERIFIED(0),
        VERIFICATION_PENDING(1),
        VERIFIED(2),
        VERIFICATION_FAILED(-1),
        BLOCKED(-2),
    }

    override fun toString(): String {
        return "AccountEntity(id=$id, _id='${data.id}', name='${data.name}', gate='${data.gate}', merchant='${data.merchant}')"
    }
}