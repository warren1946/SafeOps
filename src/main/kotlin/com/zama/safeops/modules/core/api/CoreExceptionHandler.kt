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
        .body(
            ApiResponse.error(
                code = ex.code,
                message = ex.userMessage
            )
        )

    @ExceptionHandler(ObjectOptimisticLockingFailureException::class)
    fun handleOptimisticLock(ex: ObjectOptimisticLockingFailureException): ResponseEntity<ApiResponse<Nothing>> = ResponseEntity
        .status(HttpStatus.CONFLICT)
        .body(
            ApiResponse.error(
                code = ErrorCodes.CORE_CONFLICT,
                message = "The resource was modified by another user. Please refresh and try again."
            )
        )

}