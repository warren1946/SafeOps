/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 * Permission granted for use, modification, and distribution with attribution.
 * No warranty provided.
 */

package com.zama.safeops.modules.shared.api

object ErrorCodes {
    const val VALIDATION = "VALIDATION_001"
    const val INVALID_INPUT = "VALIDATION_002"

    const val AUTH_USER_EXISTS = "AUTH_001"
    const val AUTH_INVALID_CREDENTIALS = "AUTH_002"
    const val AUTH_INVALID_TOKEN = "AUTH_003"
    const val AUTH_EXPIRED_TOKEN = "AUTH_004"

    const val CORE_NOT_FOUND = "CORE_404"
    const val GENERIC = "GENERIC_500"
}