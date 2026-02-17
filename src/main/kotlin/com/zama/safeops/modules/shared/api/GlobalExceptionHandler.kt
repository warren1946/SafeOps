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
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(UserAlreadyExistsException::class)
    fun handleUserExists(ex: UserAlreadyExistsException) =
        ResponseEntity
            .status(HttpStatus.CONFLICT)
            .body(
                ApiResponse<Unit>(
                    success = false,
                    message = ex.message ?: "User already exists"
                )
            )

    @ExceptionHandler(NotFoundException::class)
    fun handleNotFound(ex: NotFoundException) =
        ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(
                ApiResponse<Unit>(
                    success = false,
                    message = ex.message ?: "Resource not found"
                )
            )

    @ExceptionHandler(Exception::class)
    fun handleGeneric(ex: Exception) =
        ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(
                ApiResponse<Unit>(
                    success = false,
                    message = ex.message ?: "Internal server error"
                )
            )
}