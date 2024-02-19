package com.smartpayments.superpos.aggregationservice.business

import com.smartpayments.superpos.aggregationservice.repository.jsonb.TaskRepository
import com.smartpayments.superpos.aggregationservice.repository.jsonb.entity.TaskEntity
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class TaskService(private val taskRepository: TaskRepository) {

    fun get(taskId : Long) : TaskEntity{
        return taskRepository.getTaskById(taskId)
    }

    fun approve(task: TaskEntity, props: Map<String, Any>) : TaskEntity{
        task.status=2
        task.customProps = props
        return taskRepository.saveAndFlush(task)
    }

    fun create(
        type: TaskEntity.EAggregationServiceTaskType,
        status: Int=1,
        createdAt: String = LocalDateTime.now().toString(),
        customProps: Map<String,Any>?=null
    ): TaskEntity {
        return taskRepository.saveAndFlush(TaskEntity(
            type=type,
            status = status,
            createdAt = createdAt,
            customProps = customProps
        ))
    }

    fun decline(task: TaskEntity, error: String): TaskEntity {
        task.status=-1
        task.error = error
        return taskRepository.saveAndFlush(task)
    }
}