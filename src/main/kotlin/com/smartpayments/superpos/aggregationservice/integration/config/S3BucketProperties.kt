package com.smartpayments.superpos.aggregationservice.integration.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "s3")
data class S3BucketProperties(
    var key: String? = null,
    var secret: String? = null,
    var url: String? = null,
    var bucket: String? = null,
    var folder: String? = null,
    var contenttype: String? = null
)