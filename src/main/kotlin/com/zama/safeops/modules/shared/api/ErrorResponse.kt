/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 * Permission granted for use, modification, and distribution with attribution.
 * No warranty provided.
 */

package com.zama.safeops.modules.shared.api

data class ErrorResponse(
    val success: Boolean = false,
    val code: String,                 // e.g., AUTH_001, CORE_003
    val message: String,
    val errors: Map<String, String> = emptyMap()
)