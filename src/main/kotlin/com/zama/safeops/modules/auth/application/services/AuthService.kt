/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 * Permission granted for use, modification, and distribution with attribution.
 * No warranty provided.
 */

package com.zama.safeops.modules.auth.application.services

import com.zama.safeops.modules.auth.application.exceptions.InvalidCredentialsException
import com.zama.safeops.modules.auth.application.ports.UserPort
import com.zama.safeops.modules.auth.domain.valueobjects.Email
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

data class AuthTokens(
    val accessToken: String,
    val refreshToken: String
)

@Service
class AuthService(
    private val userPort: UserPort,
    private val passwordEncoder: PasswordEncoderPort,
    private val tokenService: TokenService
) {

    @Transactional(readOnly = true)
    fun login(email: String, rawPassword: String): AuthTokens {
        val user = userPort.findByEmail(Email(email))
            ?: throw InvalidCredentialsException("Invalid email or password")

        if (!passwordEncoder.matches(rawPassword, user.password.value)) {
            throw InvalidCredentialsException("Invalid email or password")
        }

        return AuthTokens(
            accessToken = tokenService.generateAccessToken(user),
            refreshToken = tokenService.generateRefreshToken(user)
        )
    }

    @Transactional(readOnly = true)
    fun refresh(refreshToken: String): AuthTokens {
        val userId = tokenService.parseUserIdFromRefreshToken(refreshToken)
        val user = userPort.findById(com.zama.safeops.modules.auth.domain.valueobjects.UserId(userId))
            ?: throw InvalidCredentialsException("Invalid refresh token")

        return AuthTokens(
            accessToken = tokenService.generateAccessToken(user),
            refreshToken = tokenService.generateRefreshToken(user)
        )
    }
}