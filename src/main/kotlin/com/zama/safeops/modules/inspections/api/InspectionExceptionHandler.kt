/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 * Permission granted for use, modification, and distribution with attribution.
 * No warranty provided.
 */

package com.zama.safeops.modules.inspections.api

import com.zama.safeops.modules.inspections.application.exceptions.InspectionException
import com.zama.safeops.modules.shared.api.ApiResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice(basePackages = ["com.zama.safeops.modules.inspections"])
class InspectionExceptionHandler {

    @ExceptionHandler(InspectionException::class)
    fun handleInspectionException(ex: InspectionException): ResponseEntity<ApiResponse<Nothing>> =
        ResponseEntity
            .status(ex.httpStatus)
            .body(
                ApiResponse.error(
                    code = ex.code,
                    message = ex.userMessage
                )
            )
}