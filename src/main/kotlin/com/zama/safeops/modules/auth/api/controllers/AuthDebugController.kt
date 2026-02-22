/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 * Permission granted for use, modification, and distribution with attribution.
 * No warranty provided.
 */

package com.zama.safeops.modules.auth.api.controllers

import com.zama.safeops.modules.auth.api.mappers.toResponse
import com.zama.safeops.modules.auth.infrastructure.rbac.CurrentUserProvider
import com.zama.safeops.modules.shared.api.ApiController
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/auth")
class AuthDebugController(
    private val currentUserProvider: CurrentUserProvider
) : ApiController() {

    @GetMapping("/me")
    fun me() = ok(
        "Current authenticated user",
        currentUserProvider.getCurrentUser()?.toResponse()
    )
}