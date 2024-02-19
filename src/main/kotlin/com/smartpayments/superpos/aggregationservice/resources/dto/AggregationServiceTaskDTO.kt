package com.smartpayments.superpos.aggregationservice.resources.dto

import com.smartpayments.superpos.aggregationservice.repository.jsonb.entity.TaskEntity

data class AggregationServiceTaskDTO(
    val id: Long,
    val status: Int,
    val type: TaskEntity.EAggregationServiceTaskType,
    val error: String?,
    val customProps: Map<String,Any>?
)