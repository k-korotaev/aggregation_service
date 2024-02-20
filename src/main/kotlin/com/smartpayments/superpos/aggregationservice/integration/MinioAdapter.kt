package com.smartpayments.superpos.aggregationservice.integration

import com.smartpayments.superpos.aggregationservice.integration.config.S3BucketProperties
import com.smartpayments.superpos.aggregationservice.integration.exception.S3IntegrationException
import io.github.oshai.kotlinlogging.KotlinLogging
import io.minio.*
import io.minio.messages.Bucket
import org.apache.commons.compress.utils.IOUtils
import org.springframework.stereotype.Component
import java.io.*


@Component
class MinioAdapter(
    val client: MinioClient,
    val s3params: S3BucketProperties) : S3Adapter {

    private val logger = KotlinLogging.logger {}

    override fun uploadToBucket(name: String, content: ByteArray): S3Adapter.S3Response {
        try {
            val stream = ByteArrayInputStream(content)
            val response = client.putObject(
                PutObjectArgs.builder()
                    .bucket(s3params.bucket)
                    .`object`(name)
                    .contentType(s3params.contenttype)
                    .stream(stream, content.size.toLong(), -1)
                    .build()
            )
            val stat = client.statObject(
                StatObjectArgs.builder()
                    .`object`(name)
                    .bucket(s3params.bucket)
                    .build());
            logger.info { "Object $name successfully uploaded at ${stat.lastModified()}." }
            return S3Adapter.S3Response(stat.bucket(), stat.`object`(), stat.size());
        } catch (ex: Exception) {
            logger.error { "${ex.message}" }
            throw S3IntegrationException("An unexpected exception occurred while loading a document.",ex)
        }
    }

    override fun getObject(key: String): ByteArray? {
        try {
            val obj: InputStream = client.getObject(
                GetObjectArgs.builder()
                    .bucket(s3params.bucket)
                    .`object`(key)
                    .build())
            val content: ByteArray = IOUtils.toByteArray(obj)
            obj.close()
            return content
        } catch (ex: Exception) {
            logger.error { "${ex.message}" }
            throw S3IntegrationException("An unexpected exception occurred while downloading a document",ex)
        }
        return null
    }
}