package com.smartpayments.superpos.aggregationservice.application.config

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.task.TaskExecutor
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor


@Configuration
class TaskExecutorConfig {

    @ConfigurationProperties(prefix = "task.executor.pool")
    data class ConfigProperties(
        val coreSize: Int,
        val maxSize: Int,
        val keepAlive: Int)

    @Bean
    @Qualifier("applicationTaskExecutor")
    fun taskExecutor(config: ConfigProperties): TaskExecutor {
        val executor = ThreadPoolTaskExecutor()
        executor.corePoolSize = config.coreSize
        executor.maxPoolSize = config.maxSize
        executor.keepAliveSeconds = config.keepAlive
        executor.setThreadNamePrefix("task-thread")
        executor.initialize() // Инициализация пула потоков

        return executor
    }
}