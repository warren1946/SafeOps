/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 * Permission granted for use, modification, and distribution with attribution.
 * No warranty provided.
 */

package com.zama.safeops.modules.shared.api

data class ApiResponse<T>(
    val success: Boolean = true,
    val message: String,
    val data: T? = null,
    val code: String? = null,
    val errors: Map<String, String>? = null
) {
    companion object {

        fun <T> ok(message: String, data: T): ApiResponse<T> =
            ApiResponse(
                success = true,
                message = message,
                data = data
            )

        fun <T> created(message: String, data: T): ApiResponse<T> =
            ApiResponse(
                success = true,
                message = message,
                data = data
            )

        fun error(
            code: String,
            message: String,
            errors: Map<String, String>? = null
        ): ApiResponse<Nothing> =
            ApiResponse(
                success = false,
                message = message,
                code = code,
                errors = errors
            )
    }
}