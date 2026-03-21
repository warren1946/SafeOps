/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 * Permission granted for use, modification, and distribution with attribution.
 * No warranty provided.
 */

package com.zama.safeops.modules.auth.application.services

import com.zama.safeops.modules.auth.application.ports.RolePort
import com.zama.safeops.modules.auth.application.ports.UserPort
import com.zama.safeops.modules.auth.domain.exceptions.NotFoundException
import com.zama.safeops.modules.auth.domain.exceptions.UserAlreadyExistsException
import com.zama.safeops.modules.auth.domain.model.Role
import com.zama.safeops.modules.auth.domain.model.User
import com.zama.safeops.modules.auth.domain.valueobjects.Email
import com.zama.safeops.modules.auth.domain.valueobjects.PasswordHash
import com.zama.safeops.modules.auth.domain.valueobjects.RoleName
import com.zama.safeops.modules.auth.domain.valueobjects.UserId
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserService(
    private val userPort: UserPort,
    private val rolePort: RolePort,
    private val passwordEncoder: PasswordEncoderPort
) {

    companion object {
        // Roles that CANNOT be self-assigned during registration
        private val RESTRICTED_ROLES = setOf("SUPER_ADMIN", "ADMIN")

        // Default role for new registrations
        private val DEFAULT_ROLE = "OFFICER"
    }

    @Transactional
    fun register(email: String, rawPassword: String, roleNames: Set<String>): User {
        val emailVo = Email(email)
        if (userPort.existsByEmail(emailVo)) {
            throw UserAlreadyExistsException(email)
        }

        // SECURITY: Filter out restricted roles to prevent privilege escalation
        val sanitizedRoles = roleNames.filter { role ->
            if (role in RESTRICTED_ROLES) {
                // Log the attempt (in real app, use proper logging)
                println("SECURITY: Attempted to assign restricted role '$role' during registration - filtered")
                false
            } else {
                true
            }
        }.toSet()

        // If no valid roles remain, assign default role
        val finalRoles = if (sanitizedRoles.isEmpty()) {
            setOf(DEFAULT_ROLE)
        } else {
            sanitizedRoles
        }

        val roles: Set<Role> = finalRoles.map { name ->
            val roleName = RoleName(name)
            rolePort.findByName(roleName)
                ?: rolePort.save(Role(id = null, name = roleName))
        }.toSet()

        val user = User(
            id = null,
            email = emailVo,
            password = PasswordHash(passwordEncoder.encode(rawPassword)),
            enabled = true,
            roles = roles
        )

        return userPort.save(user)
    }

    @Transactional(readOnly = true)
    fun listUsers(): List<User> = userPort.findAll()

    @Transactional(readOnly = true)
    fun getUser(id: Long): User =
        userPort.findById(UserId(id)) ?: throw NotFoundException("User $id not found")
}