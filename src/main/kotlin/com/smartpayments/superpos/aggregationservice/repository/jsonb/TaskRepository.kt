package com.smartpayments.superpos.aggregationservice.repository.jsonb

import com.smartpayments.superpos.aggregationservice.repository.jsonb.entity.TaskEntity
import com.smartpayments.superpos.aggregationservice.repository.jsonb.entity.TaskEntity.*
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository


@Repository
interface TaskRepository : JpaRepository<TaskEntity,Long>{
    fun getTaskById(id: Long) : TaskEntity?

    @Query("SELECT * FROM aggregation_service_tasks " +
            "WHERE status = :taskStatus " +
            "AND type = :taskType\\:\\:int " +
            "ORDER BY created_at DESC " +
            "LIMIT 1 ", nativeQuery = true)
    fun findFirstByStatusOrderByCreatedAtAsc(
        @Param("taskStatus")status: TaskStatus,
        @Param("taskType") type: EAggregationServiceTaskType): TaskEntity?

    @Query("SELECT * FROM aggregation_service_tasks " +
            "WHERE request\\:\\:jsonb = :requestData\\:\\:jsonb " +
            "AND type = :taskType\\:\\:int " +
            "ORDER BY created_at " +
            "LIMIT 1 ", nativeQuery = true)
    fun getLastTaskByTypeWithParams(
        @Param("requestData") request: String?,
        @Param("taskType") type: EAggregationServiceTaskType): TaskEntity?
}