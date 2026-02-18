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
import com.zama.safeops.modules.shared.api.ApiResponse
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/auth")
class AuthController(private val userService: UserService, private val authService: AuthService) : ApiController() {

    @PostMapping("/register")
    fun register(@Valid @RequestBody req: RegisterRequest): ApiResponse<UserResponse> {
        val user = userService.register(req.email, req.password, req.roles).toResponse()
        return ok("User registered successfully", user)
    }

    @PostMapping("/login")
    fun login(@Valid @RequestBody req: LoginRequest): ApiResponse<AuthResponse> {
        val tokens = authService.login(req.email, req.password)
        val response = AuthResponse(tokens.accessToken, tokens.refreshToken)
        return ok("Login successful", response)
    }

    @PostMapping("/refresh")
    fun refresh(@Valid @RequestBody req: RefreshRequest): ApiResponse<AuthResponse> {
        val tokens = authService.refresh(req.refreshToken)
        val response = AuthResponse(tokens.accessToken, tokens.refreshToken)
        return ok("Token refreshed successfully", response)
    }
}