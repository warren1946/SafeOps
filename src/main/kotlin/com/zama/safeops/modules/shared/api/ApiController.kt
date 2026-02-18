/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 * Permission granted for use, modification, and distribution with attribution.
 * No warranty provided.
 */

package com.zama.safeops.modules.shared.api

abstract class ApiController {
    protected fun <T> ok(message: String, data: T): ApiResponse<T> = ApiResponse(success = true, message = message, data = data)
    protected fun <T> created(message: String, data: T): ApiResponse<T> = ApiResponse(success = true, message = message, data = data)

}