/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 * Permission granted for use, modification, and distribution with attribution.
 * No warranty provided.
 */

package com.zama.safeops.modules.safety.api

import com.zama.safeops.modules.safety.application.exceptions.SafetyException
import com.zama.safeops.modules.shared.api.ApiResponse
import io.swagger.v3.oas.annotations.Hidden
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@Hidden
@RestControllerAdvice(basePackages = ["com.zama.safeops.modules.safety"])
class SafetyExceptionHandler {

    @ExceptionHandler(SafetyException::class)
    fun handleSafetyException(ex: SafetyException): ResponseEntity<ApiResponse<Unit>> = ResponseEntity
        .status(ex.httpStatus)
        .body(
            ApiResponse.error(
                code = ex.code,
                message = ex.userMessage
            )
        )
}