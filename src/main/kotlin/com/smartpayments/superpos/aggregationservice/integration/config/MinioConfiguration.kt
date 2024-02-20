package com.smartpayments.superpos.aggregationservice.integration.config

import io.minio.MinioClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class MinioConfiguration(val s3params: S3BucketProperties) {

    @Bean
    fun minioClient(): MinioClient {
        try {
            val client: MinioClient = MinioClient.builder()
                .endpoint(s3params.url)
                .credentials(s3params.key, s3params.secret)
                .build()
            return client
        } catch (e: Exception) {
            throw RuntimeException(e.message)
        }
    }
}