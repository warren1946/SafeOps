/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 * Permission granted for use, modification, and distribution with attribution.
 * No warranty provided.
 */

package com.zama.safeops.modules.auth.api

import com.zama.safeops.modules.auth.domain.exceptions.AuthException
import com.zama.safeops.modules.shared.api.ApiResponse
import io.swagger.v3.oas.annotations.Hidden
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@Hidden
@RestControllerAdvice(basePackages = ["com.zama.safeops.modules.auth"])
class AuthExceptionHandler {

    @ExceptionHandler(AuthException::class)
    fun handleAuthException(ex: AuthException): ResponseEntity<ApiResponse<Unit>> = ResponseEntity
        .status(ex.httpStatus)
        .body(
            ApiResponse.error(
                code = ex.code,
                message = ex.userMessage
            )
        )
}