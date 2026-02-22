/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 * Permission granted for use, modification, and distribution with attribution.
 * No warranty provided.
 */

package com.zama.safeops.modules.auth.application.services

import com.zama.safeops.modules.auth.application.ports.UserPort
import com.zama.safeops.modules.auth.domain.exceptions.UserNotFoundException
import com.zama.safeops.modules.auth.domain.model.Role
import com.zama.safeops.modules.auth.domain.model.User
import com.zama.safeops.modules.auth.domain.valueobjects.UserId
import com.zama.safeops.modules.auth.infrastructure.persistence.jpa.repositories.SpringDataRoleRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserManagementService(private val userPort: UserPort, private val roleRepo: SpringDataRoleRepository) {

    @Transactional(readOnly = true)
    fun listUsers(query: String?): List<User> =
        if (query.isNullOrBlank()) userPort.findAll()
        else userPort.searchByEmail(query)

    @Transactional
    fun updateStatus(id: Long, enabled: Boolean): User {
        val user = userPort.findById(UserId(id)) ?: throw UserNotFoundException(id)
        return userPort.save(user.copy(enabled = enabled))
    }

    @Transactional
    fun updateRoles(id: Long, roleNames: Set<String>): User {
        val user = userPort.findById(UserId(id)) ?: throw UserNotFoundException(id)

        val roleEntities = roleRepo.findByNameIn(roleNames)

        val updatedRoles = roleEntities.map {
            Role(
                id = it.toDomain().id,      // RoleId
                name = it.toDomain().name   // RoleName
            )
        }.toSet()

        return userPort.save(user.copy(roles = updatedRoles))
    }
}