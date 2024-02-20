package com.smartpayments.superpos.aggregationservice.business

import com.fasterxml.jackson.databind.ObjectMapper
import com.smartpayments.superpos.aggregationservice.integration.S3Adapter
import com.smartpayments.superpos.aggregationservice.repository.jsonb.entity.TaskEntity
import org.springframework.stereotype.Service


@Service
class StorageService(
    val s3Adapter: S3Adapter,
    val objectMapper: ObjectMapper
) {
    fun uploadObjects(task: TaskEntity, content: List<Any>): S3Adapter.S3Response {
        val bytearray = objectMapper.writer().writeValueAsBytes(content)
        return s3Adapter.uploadToBucket("${task.type}_${task.id.toString()}", bytearray)
    }
    fun getObjects(task: TaskEntity): ByteArray? {
        val content = s3Adapter.getObject("${task.type}_${task.id.toString()}")
        return content
    }
}