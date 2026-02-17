/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 * Permission granted for use, modification, and distribution with attribution.
 * No warranty provided.
 */

package com.zama.safeops.modules.auth.application.services

import com.zama.safeops.modules.auth.domain.model.User

interface TokenService {
    fun generateAccessToken(user: User): String
    fun generateRefreshToken(user: User): String
    fun parseUserIdFromAccessToken(token: String): Long
    fun parseUserIdFromRefreshToken(token: String): Long
}