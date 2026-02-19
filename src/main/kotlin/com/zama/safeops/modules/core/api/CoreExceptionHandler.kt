/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 * Permission granted for use, modification, and distribution with attribution.
 * No warranty provided.
 */

package com.zama.safeops.modules.core.api

import com.zama.safeops.modules.core.domain.exceptions.CoreException
import com.zama.safeops.modules.shared.api.ApiResponse
import com.zama.safeops.modules.shared.constants.ErrorCodes
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.orm.ObjectOptimisticLockingFailureException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice(basePackages = ["com.zama.safeops.modules.core"])
class CoreExceptionHandler {

    @ExceptionHandler(CoreException::class)
    fun handleCoreException(ex: CoreException): ResponseEntity<ApiResponse<Nothing>> = ResponseEntity
        .status(ex.httpStatus)
        .body(ApiResponse.error(ex.code, ex.userMessage))

    @ExceptionHandler(ObjectOptimisticLockingFailureException::class)
    fun handleOptimisticLock(ex: ObjectOptimisticLockingFailureException): ResponseEntity<ApiResponse<Nothing>> = ResponseEntity
        .status(HttpStatus.CONFLICT)
        .body(
            ApiResponse.error(
                code = ErrorCodes.CORE_CONFLICT,
                message = "The resource was modified by another user. Please refresh and try again."
            )
        )

    @ExceptionHandler(DataIntegrityViolationException::class)
    fun handleDataIntegrity(ex: DataIntegrityViolationException): ResponseEntity<ApiResponse<Nothing>> {

        val constraint = extractConstraintName(ex)
        val message = when (constraint) {

            // MINE
            "mine_code_key" -> "A mine with this code already exists"
            "mine_name_key" -> "A mine with this name already exists"

            // SITE
            "site_code_key" -> "A site with this code already exists"
            "site_name_key" -> "A site with this name already exists"

            // SHAFT
            "shaft_code_key" -> "A shaft with this code already exists"
            "shaft_name_key" -> "A shaft with this name already exists"

            // AREA
            "area_code_key" -> "An area with this code already exists"
            "area_name_key" -> "An area with this name already exists"

            else -> "Data integrity violation"
        }

        return ResponseEntity
            .status(HttpStatus.CONFLICT)
            .body(
                ApiResponse.error(
                    code = ErrorCodes.CORE_DUPLICATE,
                    message = message
                )
            )
    }

    private fun extractConstraintName(ex: DataIntegrityViolationException): String? {
        val msg = ex.rootCause?.message ?: ex.message ?: return null

        // PostgreSQL format: duplicate key value violates unique constraint "mine_code_key"
        val regex = Regex("unique constraint \"([^\"]+)\"", RegexOption.IGNORE_CASE)
        val match = regex.find(msg)
        return match?.groups?.get(1)?.value
    }
}