package com.smartpayments.superpos.aggregationservice.application.config

import com.smartpayments.superpos.aggregationservice.repository.jsonb.entity.TaskEntity.TaskStatus
import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter
import org.springframework.context.annotation.Configuration

@Configuration
class JPAConfig {
    @Converter(autoApply = true)
    inner class StatusConverter : AttributeConverter<TaskStatus, Int> {
        override fun convertToDatabaseColumn(taskStatus: TaskStatus): Int {
            return taskStatus.value
        }

        override fun convertToEntityAttribute(dbData: Int): TaskStatus {
            return TaskStatus.valueOf(dbData.toString())
        }
    }
}