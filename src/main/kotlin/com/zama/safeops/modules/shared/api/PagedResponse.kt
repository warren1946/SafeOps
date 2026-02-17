/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 * Permission granted for use, modification, and distribution with attribution.
 * No warranty provided.
 */

package com.zama.safeops.modules.shared.api

data class PagedResponse<T>(
    val success: Boolean = true,
    val message: String = "OK",
    val data: List<T>,
    val page: Int,
    val size: Int,
    val total: Long,
    val totalPages: Int
)