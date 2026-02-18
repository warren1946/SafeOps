/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 * Permission granted for use, modification, and distribution with attribution.
 * No warranty provided.
 */

package com.zama.safeops.modules.shared.api

abstract class PagedController : ApiController() {
    protected fun <T> paged(message: String, data: List<T>, page: Int, size: Int, total: Long): PagedResponse<T> = PagedResponse(
        success = true,
        message = message,
        data = data,
        page = page,
        size = size,
        total = total,
        totalPages = ((total + size - 1) / size).toInt()
    )
}