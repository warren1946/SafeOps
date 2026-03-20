/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 * Permission granted for use, modification, and distribution with attribution.
 * No warranty provided.
 */

package com.zama.safeops.modules.auth.api.controllers

import com.zama.safeops.modules.auth.api.dto.*
import com.zama.safeops.modules.auth.api.mappers.toResponse
import com.zama.safeops.modules.auth.application.services.AuthService
import com.zama.safeops.modules.auth.application.services.UserService
import com.zama.safeops.modules.shared.api.ApiController
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import com.zama.safeops.modules.shared.api.ApiResponse as SafeOpsApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponse as SwaggerApiResponse

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Authentication", description = "User authentication and registration endpoints")
class AuthController(
    private val userService: UserService,
    private val authService: AuthService
) : ApiController() {

    @Operation(
        summary = "Register a new user",
        description = "Creates a new user account with the provided email and password"
    )
    @ApiResponses(
        value = [
            SwaggerApiResponse(
                responseCode = "200",
                description = "User registered successfully",
                content = [Content(schema = Schema(implementation = SafeOpsApiResponse::class))]
            ),
            SwaggerApiResponse(
                responseCode = "400",
                description = "Invalid input - email already exists or validation failed",
                content = [Content(schema = Schema(implementation = SafeOpsApiResponse::class))]
            ),
            SwaggerApiResponse(
                responseCode = "409",
                description = "User with this email already exists",
                content = [Content(schema = Schema(implementation = SafeOpsApiResponse::class))]
            )
        ]
    )
    @PostMapping("/register")
    fun register(
        @Valid @RequestBody req: RegisterRequest
    ): ResponseEntity<SafeOpsApiResponse<UserResponse>> = ok(
        "User registered successfully",
        userService.register(req.email, req.password, req.roles).toResponse()
    )

    @Operation(
        summary = "Authenticate user",
        description = "Authenticates a user and returns JWT access and refresh tokens"
    )
    @ApiResponses(
        value = [
            SwaggerApiResponse(
                responseCode = "200",
                description = "Login successful",
                content = [Content(schema = Schema(implementation = SafeOpsApiResponse::class))]
            ),
            SwaggerApiResponse(
                responseCode = "401",
                description = "Invalid credentials",
                content = [Content(schema = Schema(implementation = SafeOpsApiResponse::class))]
            ),
            SwaggerApiResponse(
                responseCode = "400",
                description = "Invalid input format",
                content = [Content(schema = Schema(implementation = SafeOpsApiResponse::class))]
            )
        ]
    )
    @PostMapping("/login")
    fun login(
        @Valid @RequestBody req: LoginRequest
    ): ResponseEntity<SafeOpsApiResponse<AuthResponse>> {
        val tokens = authService.login(req.email, req.password)
        val response = AuthResponse(tokens.accessToken, tokens.refreshToken)
        return ok("Login successful", response)
    }

    @Operation(
        summary = "Refresh access token",
        description = "Generates a new access token using a valid refresh token"
    )
    @ApiResponses(
        value = [
            SwaggerApiResponse(
                responseCode = "200",
                description = "Token refreshed successfully",
                content = [Content(schema = Schema(implementation = SafeOpsApiResponse::class))]
            ),
            SwaggerApiResponse(
                responseCode = "401",
                description = "Invalid or expired refresh token",
                content = [Content(schema = Schema(implementation = SafeOpsApiResponse::class))]
            )
        ]
    )
    @PostMapping("/refresh")
    fun refresh(
        @Valid @RequestBody req: RefreshRequest
    ): ResponseEntity<SafeOpsApiResponse<AuthResponse>> {
        val tokens = authService.refresh(req.refreshToken)
        val response = AuthResponse(tokens.accessToken, tokens.refreshToken)
        return ok("Token refreshed successfully", response)
    }
}
