package com.smartpayments.superpos.aggregationservice.application

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@SpringBootApplication
@EntityScan("com.smartpayments.superpos.aggregationservice.repository.jsonb.entity")
@EnableJpaRepositories("com.smartpayments.superpos.aggregationservice.repository.jsonb")
@ComponentScan("com.smartpayments.superpos.aggregationservice")
class AggregationServiceApplication

fun main(args: Array<String>) {
	runApplication<AggregationServiceApplication>(*args)
}
