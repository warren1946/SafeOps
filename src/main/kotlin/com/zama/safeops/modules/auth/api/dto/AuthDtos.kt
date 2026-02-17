/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 * Permission granted for use, modification, and distribution with attribution.
 * No warranty provided.
 */

package com.zama.safeops.modules.auth.api.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class RegisterRequest(
    @field:NotBlank(message = "Email is required")
    @field:Email(message = "Email must be a valid email address")
    val email: String,
    @field:NotBlank(message = "Password is required")
    @field:Size(min = 6, max = 100, message = "Password must be between 6 and 100 characters")
    val password: String,
    val roles: Set<String> = setOf("ROLE_USER")
)

data class LoginRequest(
    @field:NotBlank(message = "Email is required")
    @field:Email(message = "Email must be a valid email address")
    val email: String,
    @field:NotBlank(message = "Password is required")
    val password: String
)

data class RefreshRequest(
    @field:NotBlank(message = "Refresh token is required")
    val refreshToken: String
)

data class AuthResponse(
    val accessToken: String,
    val refreshToken: String
)

data class UserResponse(
    val id: Long,
    val email: String,
    val enabled: Boolean,
    val roles: Set<String>
)