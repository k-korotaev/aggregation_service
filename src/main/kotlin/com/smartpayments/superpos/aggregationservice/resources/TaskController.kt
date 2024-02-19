package com.smartpayments.superpos.aggregationservice.resources

import com.smartpayments.superpos.aggregationservice.business.TaskService
import com.smartpayments.superpos.aggregationservice.resources.dto.AggregationServiceTaskDTO
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/task")
class TaskController(
    private val taskService: TaskService) {
    private val logger = KotlinLogging.logger {}

    @GetMapping("/{id}", produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseBody
    fun get(@PathVariable id: Long): AggregationServiceTaskDTO {
        val task = taskService.get(id)
        return AggregationServiceTaskDTO(task.id!!,task.status,task.type,task.error,task.customProps)
    }

    @GetMapping("/{id}/result", produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseBody
    fun getTaskResult(@PathVariable id: Long): Map<String, Any>? {
        val task = taskService.get(id)
        return task.customProps
    }
}