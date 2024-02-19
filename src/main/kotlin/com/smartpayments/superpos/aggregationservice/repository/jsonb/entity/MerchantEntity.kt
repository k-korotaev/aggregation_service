package com.smartpayments.superpos.aggregationservice.repository.jsonb.entity

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.persistence.Column
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes

class MerchantEntity (
    val id: String,
    @Column(name = "document", columnDefinition = "json")
    @JdbcTypeCode(SqlTypes.JSON)
    val data: Merchant
){

    @JsonIgnoreProperties(ignoreUnknown = true)
    data class Merchant(

        @JsonProperty("_id")
        var id: String?,

        var username: String?,

        var name: String?,

        var email: String?,

        var password: String?,

        var persistentToken: String?,

        var isConfirmed: Boolean? = true,

        var verifyStatus: Int? = 0,

        var system: MerchantSystem?,

        var credentials: MerchantCredentials?,

        @JsonProperty("Merchants")
        var childMerchants: List<String>?,

        var role: EMerchantRole?,

        var type: EMerchantType?,

        var descriptor: String?,

        @field:Column(name = "whiteAddresses")
        var whiteAddresses: List<String>?,

        @field:Column(name = "isEnabledWhiteAddressFilter")
        var isEnabledWhiteAddressFilter: Boolean? = false,

        @field:Column(name = "auth0Ids")
        var auth0Ids: List<String>? = null
    )

    data class MerchantCredentials(
        @field:Column(name = "legalName")
        var legalName: String?,

        @field:Column(name = "merchantName")
        var merchantName: String? = null
    )

    data class MerchantSystem(
        @field:Column(name = "merchantKey")
        var merchantKey: String?,

        var secret: String?,

        var active: Boolean? = false
    )

    enum class EMerchantRole(val role:String)  {
        USER("user"),
        AGGREGATOR("aggregator"),
    }

    enum class EMerchantType(val type:String) {
        CNP_MERCHANT("cnp_merchant"),
        SMARTPOS_MERCHANT("smartpos_merchant"),
    }

}