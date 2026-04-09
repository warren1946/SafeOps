/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 * Permission granted for use, modification, and distribution with attribution.
 * No warranty provided.
 */

package com.zama.safeops.modules.auth.api.dto

import com.zama.safeops.config.validation.StrictEmail
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

private const val MAX_EMAIL_LENGTH = 254
private const val MAX_PASSWORD_LENGTH = 128
private const val MAX_TOKEN_LENGTH = 4096

data class RegisterRequest(
    @field:NotBlank(message = "Email is required")
    @field:Size(max = MAX_EMAIL_LENGTH, message = "Email must not exceed $MAX_EMAIL_LENGTH characters")
    @field:Email(message = "Email must be a valid email address")
    @field:StrictEmail
    val email: String,

    @field:NotBlank(message = "Password is required")
    @field:Size(min = 8, max = MAX_PASSWORD_LENGTH, message = "Password must be between 8 and $MAX_PASSWORD_LENGTH characters")
    @field:Pattern(
        regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]+$",
        message = "Password must contain at least one uppercase letter, one lowercase letter, one digit, and one special character"
    )
    val password: String,

    @field:Size(max = 10, message = "Cannot assign more than 10 roles")
    val roles: Set<String> = setOf("ROLE_USER")
) {
    init {
        // Additional validation in init block
        require(email.length <= MAX_EMAIL_LENGTH) { "Email exceeds maximum length" }
        require(password.length in 8..MAX_PASSWORD_LENGTH) { "Password length invalid" }
        require(roles.all { it.matches(Regex("^[A-Z_]+$")) }) { "Invalid role format" }
    }
}

data class LoginRequest(
    @field:NotBlank(message = "Email is required")
    @field:Size(max = MAX_EMAIL_LENGTH, message = "Email must not exceed $MAX_EMAIL_LENGTH characters")
    @field:Email(message = "Email must be a valid email address")
    @field:StrictEmail
    val email: String,

    @field:NotBlank(message = "Password is required")
    @field:Size(max = MAX_PASSWORD_LENGTH, message = "Password must not exceed $MAX_PASSWORD_LENGTH characters")
    val password: String
) {
    init {
        require(email.length <= MAX_EMAIL_LENGTH) { "Email exceeds maximum length" }
        require(password.length <= MAX_PASSWORD_LENGTH) { "Password exceeds maximum length" }
    }
}

data class RefreshRequest(
    @field:NotBlank(message = "Refresh token is required")
    @field:Size(max = MAX_TOKEN_LENGTH, message = "Token exceeds maximum length")
    val refreshToken: String
) {
    init {
        require(refreshToken.length <= MAX_TOKEN_LENGTH) { "Token exceeds maximum length" }
    }
}

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

data class UpdateUserStatusRequest(
    val enabled: Boolean
)

data class UpdateUserRolesRequest(
    @field:Size(max = 10, message = "Cannot assign more than 10 roles")
    val roles: Set<String>
) {
    init {
        require(roles.all { it.matches(Regex("^[A-Z_]+$")) }) { "Invalid role format" }
    }
}
