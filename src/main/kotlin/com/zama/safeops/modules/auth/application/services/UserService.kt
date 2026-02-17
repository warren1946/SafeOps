/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 * Permission granted for use, modification, and distribution with attribution.
 * No warranty provided.
 */

package com.zama.safeops.modules.auth.application.services

import com.zama.safeops.modules.auth.application.exceptions.NotFoundException
import com.zama.safeops.modules.auth.application.exceptions.UserAlreadyExistsException
import com.zama.safeops.modules.auth.application.ports.RolePort
import com.zama.safeops.modules.auth.application.ports.UserPort
import com.zama.safeops.modules.auth.domain.model.Role
import com.zama.safeops.modules.auth.domain.model.User
import com.zama.safeops.modules.auth.domain.valueobjects.*
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserService(
    private val userPort: UserPort,
    private val rolePort: RolePort,
    private val passwordEncoder: PasswordEncoderPort
) {

    @Transactional
    fun register(email: String, rawPassword: String, roleNames: Set<String>): User {
        val emailVo = Email(email)
        if (userPort.existsByEmail(emailVo)) {
            throw UserAlreadyExistsException("User with email $email already exists")
        }

        val roles: Set<Role> = roleNames.map { name ->
            val roleName = RoleName(name)
            rolePort.findByName(roleName)
                ?: rolePort.save(Role(RoleId(1), roleName))
        }.toSet()

        val user = User(
            id = UserId(1),
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