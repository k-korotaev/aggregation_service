package com.smartpayments.superpos.aggregationservice.resources.dto

import com.smartpayments.superpos.aggregationservice.repository.jsonb.entity.TaskEntity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated

data class AggregationServiceTaskDTO(
    val id: Long?=null,
    @Enumerated(EnumType.ORDINAL)
    val status: Int,
    val type: TaskEntity.EAggregationServiceTaskType,
    val error: String?,
    val customProps: Map<String,Any>?
)