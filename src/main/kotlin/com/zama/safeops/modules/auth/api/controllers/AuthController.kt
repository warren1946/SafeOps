/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 * Permission granted for use, modification, and distribution with attribution.
 * No warranty provided.
 */

package com.zama.safeops.modules.auth.api.controllers

import com.zama.safeops.modules.auth.api.dto.AuthResponse
import com.zama.safeops.modules.auth.api.dto.LoginRequest
import com.zama.safeops.modules.auth.api.dto.RefreshRequest
import com.zama.safeops.modules.auth.api.dto.RegisterRequest
import com.zama.safeops.modules.auth.api.mappers.toResponse
import com.zama.safeops.modules.auth.application.services.AuthService
import com.zama.safeops.modules.auth.application.services.UserService
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/auth")
class AuthController(
    private val userService: UserService,
    private val authService: AuthService
) {

    @PostMapping("/register")
    fun register(@Valid @RequestBody req: RegisterRequest) {
        println("REGISTER HIT")
        userService.register(req.email, req.password, req.roles).toResponse()
    }

    @PostMapping("/login")
    fun login(@Valid @RequestBody req: LoginRequest): AuthResponse {
        val tokens = authService.login(req.email, req.password)
        return AuthResponse(
            accessToken = tokens.accessToken,
            refreshToken = tokens.refreshToken
        )
    }

    @PostMapping("/refresh")
    fun refresh(@Valid @RequestBody req: RefreshRequest): AuthResponse {
        val tokens = authService.refresh(req.refreshToken)
        return AuthResponse(
            accessToken = tokens.accessToken,
            refreshToken = tokens.refreshToken
        )
    }
}