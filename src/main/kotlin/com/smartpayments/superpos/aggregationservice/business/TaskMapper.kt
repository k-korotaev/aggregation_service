package com.smartpayments.superpos.aggregationservice.business

import com.smartpayments.superpos.aggregationservice.repository.jsonb.entity.TaskEntity
import com.smartpayments.superpos.aggregationservice.resources.dto.AggregationServiceTaskDTO
import org.springframework.stereotype.Component

@Component
class TaskMapper {
 companion object {
     fun toDTO(taskEntity: TaskEntity): AggregationServiceTaskDTO {
         return AggregationServiceTaskDTO(
             taskEntity.id ?: 0,
             taskEntity.status.value,
             taskEntity.type,
             taskEntity.error,
             taskEntity.customProps
         )
     }
 }
}