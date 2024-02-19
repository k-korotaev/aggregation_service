package com.smartpayments.superpos.aggregationservice.resources.dto

data class BillingRequestDTO(
    val merchantId:String, //not Null
    val bankId:String?,
    val accountId:String?,
    val startTransactionId:String, //not Null
    val endTransactionId:String, //not Null
    val type: String?)