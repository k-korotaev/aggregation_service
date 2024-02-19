package com.smartpayments.superpos.aggregationservice.repository.jsonb

import com.smartpayments.superpos.aggregationservice.repository.jsonb.entity.TaskEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
@Repository
interface TaskRepository : JpaRepository<TaskEntity,String>{
    fun getTaskById(id: Long) : TaskEntity
}