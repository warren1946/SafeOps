/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 * Permission granted for use, modification, and distribution with attribution.
 * No warranty provided.
 */

package com.zama.safeops.modules.shared.api

import org.springframework.http.ResponseEntity

open class ApiController {
    protected fun <T> ok(message: String, data: T): ResponseEntity<ApiResponse<T>> = ResponseEntity.ok(ApiResponse.ok(message, data))
    protected fun <T> created(message: String, data: T): ResponseEntity<ApiResponse<T>> = ResponseEntity.status(201).body(ApiResponse.created(message, data))
}