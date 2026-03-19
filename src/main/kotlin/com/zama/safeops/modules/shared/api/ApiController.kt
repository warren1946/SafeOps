/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 * Permission granted for use, modification, and distribution with attribution.
 * No warranty provided.
 */

package com.zama.safeops.modules.shared.api

import com.zama.safeops.modules.auth.infrastructure.rbac.CurrentUserProvider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity

open class ApiController {

    @Autowired
    private lateinit var currentUserProvider: CurrentUserProvider

    protected fun <T> ok(message: String, data: T): ResponseEntity<ApiResponse<T>> =
        ResponseEntity.ok(ApiResponse.ok(message, data))

    protected fun <T> created(message: String, data: T): ResponseEntity<ApiResponse<T>> =
        ResponseEntity.status(201).body(ApiResponse.created(message, data))

    protected fun currentUserId(): Long =
        currentUserProvider.getCurrentUser()?.id?.value
            ?: throw IllegalStateException("No authenticated user found")

    protected fun currentUser() =
        currentUserProvider.getCurrentUser()
            ?: throw IllegalStateException("No authenticated user found")

    protected fun <T> paged(
        data: List<T>,
        total: Long,
        page: Int = 1,
        size: Int = 20
    ): ResponseEntity<PagedResponse<T>> {
        val totalPages = if (total == 0L) 1 else ((total + size - 1) / size).toInt()
        return ResponseEntity.ok(
            PagedResponse(
                success = true,
                message = "Data retrieved successfully",
                data = data,
                page = page,
                size = size,
                total = total,
                totalPages = totalPages
            )
        )
    }
}