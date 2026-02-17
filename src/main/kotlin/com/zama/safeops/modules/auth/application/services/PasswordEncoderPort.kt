/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 * Permission granted for use, modification, and distribution with attribution.
 * No warranty provided.
 */

package com.zama.safeops.modules.auth.application.services

interface PasswordEncoderPort {
    fun encode(raw: String): String
    fun matches(raw: String, encoded: String): Boolean
}