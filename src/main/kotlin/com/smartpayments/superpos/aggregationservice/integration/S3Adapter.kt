package com.smartpayments.superpos.aggregationservice.integration

interface S3Adapter {

    data class S3Response(
        val bucket:String,
        val objectName: String,
        val size: Long)

    fun getObject(key: String): ByteArray?
    fun uploadToBucket(name: String, content: ByteArray): S3Response
}