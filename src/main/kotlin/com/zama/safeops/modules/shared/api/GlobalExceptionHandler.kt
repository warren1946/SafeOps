/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 * Permission granted for use, modification, and distribution with attribution.
 * No warranty provided.
 */

package com.zama.safeops.modules.shared.api

import com.zama.safeops.modules.auth.application.exceptions.NotFoundException
import com.zama.safeops.modules.auth.application.exceptions.UserAlreadyExistsException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {

    // Validation errors (DTO @Valid failures)
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidation(ex: MethodArgumentNotValidException): ResponseEntity<ErrorResponse> {

        val errors = ex.bindingResult.fieldErrors.associate { fieldError ->
            fieldError.field to (fieldError.defaultMessage ?: "Invalid value")
        }

        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(
                ErrorResponse(
                    message = "Validation failed",
                    errors = errors
                )
            )
    }

    // User already exists
    @ExceptionHandler(UserAlreadyExistsException::class)
    fun handleUserExists(ex: UserAlreadyExistsException) =
        ResponseEntity
            .status(HttpStatus.CONFLICT)
            .body(
                ErrorResponse(
                    message = ex.message ?: "User already exists",
                    errors = emptyMap()
                )
            )

    // Not found
    @ExceptionHandler(NotFoundException::class)
    fun handleNotFound(ex: NotFoundException) =
        ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(
                ErrorResponse(
                    message = ex.message ?: "Resource not found",
                    errors = emptyMap()
                )
            )

    // Catch-all fallback
    @ExceptionHandler(Exception::class)
    fun handleGeneric(ex: Exception) =
        ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(
                ErrorResponse(
                    message = "Internal server error",
                    errors = emptyMap()
                )
            )
}