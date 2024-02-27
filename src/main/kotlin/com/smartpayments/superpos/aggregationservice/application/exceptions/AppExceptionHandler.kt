package com.smartpayments.superpos.aggregationservice.application.exceptions

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.ErrorResponse
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.WebRequest

@RestControllerAdvice
class ApplicationExceptionHandler() {
    private val logger = KotlinLogging.logger {}

    class ContentNotFoundException(message: String, cause: Throwable?=null):Exception(message, cause)

    @ExceptionHandler
    fun handleException(exception: ContentNotFoundException, request: WebRequest): ResponseEntity<ErrorResponse>
    {
        logger.info { "No content exception" };
        val errorResponse = ErrorResponse.create(exception, HttpStatus.NO_CONTENT, exception.message.toString());

        return ResponseEntity
            .status(HttpStatus.NO_CONTENT)
            .contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .body(errorResponse);
    }}