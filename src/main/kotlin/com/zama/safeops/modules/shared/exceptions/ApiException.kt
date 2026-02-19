/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 * Permission granted for use, modification, and distribution with attribution.
 * No warranty provided.
 */

package com.zama.safeops.modules.shared.exceptions

import org.springframework.http.HttpStatus

interface ApiException {
    val code: String
    val httpStatus: HttpStatus
    val userMessage: String
}