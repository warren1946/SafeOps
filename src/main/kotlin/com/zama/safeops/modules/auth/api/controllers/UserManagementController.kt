/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 * Permission granted for use, modification, and distribution with attribution.
 * No warranty provided.
 */

package com.zama.safeops.modules.auth.api.controllers

import com.zama.safeops.modules.auth.api.dto.UpdateUserRolesRequest
import com.zama.safeops.modules.auth.api.dto.UpdateUserStatusRequest
import com.zama.safeops.modules.auth.api.mappers.toResponse
import com.zama.safeops.modules.auth.application.services.UserManagementService
import com.zama.safeops.modules.auth.infrastructure.rbac.RequiresRole
import com.zama.safeops.modules.shared.api.ApiController
import org.springframework.web.bind.annotation.*

@RequiresRole("ADMIN")
@RestController
@RequestMapping("/api/users")
class UserManagementController(private val userManagementService: UserManagementService) : ApiController() {

    @GetMapping
    fun list(@RequestParam(required = false) query: String?) = ok(
        "Users retrieved successfully",
        userManagementService.listUsers(query).map { it.toResponse() }
    )

    @PatchMapping("/{id}/status")
    fun updateStatus(@PathVariable id: Long, @RequestBody req: UpdateUserStatusRequest) = ok(
        "User status updated successfully",
        userManagementService.updateStatus(id, req.enabled).toResponse()
    )

    @PutMapping("/{id}/roles")
    fun updateRoles(@PathVariable id: Long, @RequestBody req: UpdateUserRolesRequest) = ok(
        "User roles updated successfully",
        userManagementService.updateRoles(id, req.roles).toResponse()
    )
}