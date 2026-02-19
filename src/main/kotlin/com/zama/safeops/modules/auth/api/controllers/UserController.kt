/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 * Permission granted for use, modification, and distribution with attribution.
 * No warranty provided.
 */

package com.zama.safeops.modules.auth.api.controllers

import com.zama.safeops.modules.auth.api.dto.UserResponse
import com.zama.safeops.modules.auth.api.mappers.toResponse
import com.zama.safeops.modules.auth.application.services.UserService
import com.zama.safeops.modules.shared.api.ApiController
import com.zama.safeops.modules.shared.api.ApiResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/auth/users")
class UserController(private val userService: UserService) : ApiController() {

    @GetMapping
    fun list(): ResponseEntity<ApiResponse<List<UserResponse>>> = ok(
        "Users retrieved successfully",
        userService.listUsers().map { it.toResponse() }
    )

    @GetMapping("/{id}")
    fun get(@PathVariable id: Long): ResponseEntity<ApiResponse<UserResponse>> = ok(
        "User retrieved successfully",
        userService.getUser(id).toResponse()
    )
}