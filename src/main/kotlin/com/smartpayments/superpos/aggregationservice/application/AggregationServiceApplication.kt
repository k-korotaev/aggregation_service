package com.smartpayments.superpos.aggregationservice.application

import com.smartpayments.superpos.aggregationservice.application.config.TaskExecutorConfig
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableAsync
@EnableScheduling
@EntityScan("com.smartpayments.superpos.aggregationservice.repository.jsonb.entity")
@EnableJpaRepositories("com.smartpayments.superpos.aggregationservice.repository.jsonb")
@ComponentScan("com.smartpayments.superpos.aggregationservice")
@EnableConfigurationProperties(TaskExecutorConfig.ConfigProperties::class)
class AggregationServiceApplication

fun main(args: Array<String>) {
	runApplication<AggregationServiceApplication>(*args)
}
