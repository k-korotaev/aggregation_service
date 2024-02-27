package com.smartpayments.superpos.aggregationservice.resources

import com.smartpayments.superpos.aggregationservice.application.exceptions.ApplicationExceptionHandler
import com.smartpayments.superpos.aggregationservice.business.StorageService
import com.smartpayments.superpos.aggregationservice.business.TaskService
import com.smartpayments.superpos.aggregationservice.resources.dto.AggregationServiceTaskDTO
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/task")
class TaskController(
    private val taskService: TaskService,
    private val storageService: StorageService) {

    @GetMapping("/{id}", produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseBody
    fun get(@PathVariable id: Long): AggregationServiceTaskDTO {
        val task = taskService.get(id) ?: throw ApplicationExceptionHandler.ContentNotFoundException("Task $id does not exist in database")
        return AggregationServiceTaskDTO(task.id!!,task.status.value,task.type,task.error,task.customProps)
    }

    @GetMapping("/{id}/result", produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseBody
    fun getTaskResult(@PathVariable id: Long): ResponseEntity<Any> {
        val task = taskService.get(id) ?: throw ApplicationExceptionHandler.ContentNotFoundException("Task $id does not exist in database")
        return ResponseEntity(storageService.getObjects(task), HttpStatus.OK)
    }
}