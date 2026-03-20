/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 */

package com.zama.safeops.modules.auth.application.services

import com.zama.safeops.modules.auth.application.ports.UserPort
import com.zama.safeops.modules.auth.domain.exceptions.InvalidCredentialsException
import com.zama.safeops.modules.auth.domain.exceptions.InvalidTokenException
import com.zama.safeops.modules.auth.domain.model.Role
import com.zama.safeops.modules.auth.domain.model.User
import com.zama.safeops.modules.auth.domain.valueobjects.*
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.*

@DisplayName("AuthService Tests")
class AuthServiceTest {

    private lateinit var userPort: UserPort
    private lateinit var passwordEncoder: PasswordEncoderPort
    private lateinit var tokenService: TokenService
    private lateinit var authService: AuthService

    @BeforeEach
    fun setup() {
        userPort = mockk()
        passwordEncoder = mockk()
        tokenService = mockk()
        authService = AuthService(userPort, passwordEncoder, tokenService)
    }

    @Nested
    @DisplayName("Login Tests")
    inner class LoginTests {

        @Test
        @DisplayName("Should return tokens when credentials are valid")
        fun `should return tokens when credentials are valid`() {
            // Given
            val email = "user@example.com"
            val password = "password123"
            val user = createTestUser(email, password)
            val expectedAccessToken = "access_token_123"
            val expectedRefreshToken = "refresh_token_123"

            every { userPort.findByEmail(Email(email)) } returns user
            every { passwordEncoder.matches(password, password) } returns true
            every { tokenService.generateAccessToken(user) } returns expectedAccessToken
            every { tokenService.generateRefreshToken(user) } returns expectedRefreshToken

            // When
            val result = authService.login(email, password)

            // Then
            assertThat(result.accessToken).isEqualTo(expectedAccessToken)
            assertThat(result.refreshToken).isEqualTo(expectedRefreshToken)
            verify { userPort.findByEmail(Email(email)) }
            verify { passwordEncoder.matches(password, password) }
        }

        @Test
        @DisplayName("Should throw InvalidCredentialsException when user not found")
        fun `should throw InvalidCredentialsException when user not found`() {
            // Given
            val email = "nonexistent@example.com"
            val password = "password123"

            every { userPort.findByEmail(Email(email)) } returns null

            // When/Then
            assertThrows<InvalidCredentialsException> {
                authService.login(email, password)
            }
            verify { userPort.findByEmail(Email(email)) }
        }

        @Test
        @DisplayName("Should throw InvalidCredentialsException when password doesn't match")
        fun `should throw InvalidCredentialsException when password doesn't match`() {
            // Given
            val email = "user@example.com"
            val password = "wrongpassword"
            val user = createTestUser(email, "correctpassword")

            every { userPort.findByEmail(Email(email)) } returns user
            every { passwordEncoder.matches(password, "correctpassword") } returns false

            // When/Then
            assertThrows<InvalidCredentialsException> {
                authService.login(email, password)
            }
            verify { userPort.findByEmail(Email(email)) }
            verify { passwordEncoder.matches(password, "correctpassword") }
        }
    }

    @Nested
    @DisplayName("Refresh Token Tests")
    inner class RefreshTokenTests {

        @Test
        @DisplayName("Should return new tokens when refresh token is valid")
        fun `should return new tokens when refresh token is valid`() {
            // Given
            val refreshToken = "valid_refresh_token"
            val userId = 1L
            val user = createTestUser("user@example.com", "password")
            val expectedAccessToken = "new_access_token"
            val expectedRefreshToken = "new_refresh_token"

            every { tokenService.parseUserIdFromRefreshToken(refreshToken) } returns userId
            every { userPort.findById(UserId(userId)) } returns user
            every { tokenService.generateAccessToken(user) } returns expectedAccessToken
            every { tokenService.generateRefreshToken(user) } returns expectedRefreshToken

            // When
            val result = authService.refresh(refreshToken)

            // Then
            assertThat(result.accessToken).isEqualTo(expectedAccessToken)
            assertThat(result.refreshToken).isEqualTo(expectedRefreshToken)
            verify { tokenService.parseUserIdFromRefreshToken(refreshToken) }
            verify { userPort.findById(UserId(userId)) }
        }

        @Test
        @DisplayName("Should throw InvalidTokenException when user not found for refresh token")
        fun `should throw InvalidTokenException when user not found for refresh token`() {
            // Given
            val refreshToken = "invalid_refresh_token"
            val userId = 999L

            every { tokenService.parseUserIdFromRefreshToken(refreshToken) } returns userId
            every { userPort.findById(UserId(userId)) } returns null

            // When/Then
            assertThrows<InvalidTokenException> {
                authService.refresh(refreshToken)
            }
        }
    }

    private fun createTestUser(email: String, password: String): User {
        return User(
            id = UserId(1L),
            email = Email(email),
            password = PasswordHash(password),
            enabled = true,
            roles = setOf(Role(RoleId(1L), RoleName("USER")))
        )
    }
}
