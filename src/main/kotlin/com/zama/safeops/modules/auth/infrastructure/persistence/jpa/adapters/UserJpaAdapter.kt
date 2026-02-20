/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 * Permission granted for use, modification, and distribution with attribution.
 * No warranty provided.
 */

package com.zama.safeops.modules.auth.infrastructure.persistence.jpa.adapters

import com.zama.safeops.modules.auth.application.ports.UserPort
import com.zama.safeops.modules.auth.domain.model.User
import com.zama.safeops.modules.auth.domain.valueobjects.Email
import com.zama.safeops.modules.auth.domain.valueobjects.UserId
import com.zama.safeops.modules.auth.infrastructure.persistence.jpa.entities.UserJpaEntity
import com.zama.safeops.modules.auth.infrastructure.persistence.jpa.repositories.SpringDataUserRepository
import org.springframework.stereotype.Component

@Component
class UserJpaAdapter(
    private val repo: SpringDataUserRepository
) : UserPort {

    override fun save(user: User): User =
        repo.save(UserJpaEntity.fromDomain(user)).toDomain()

    override fun findByEmail(email: Email): User? =
        repo.findByEmail(email.value).orElse(null)?.toDomain()

    override fun existsByEmail(email: Email): Boolean =
        repo.existsByEmail(email.value)

    override fun findById(id: UserId): User? =
        repo.findById(id.value).orElse(null)?.toDomain()

    override fun existsById(id: UserId): Boolean =
        repo.existsById(id.value)

    override fun findAll(): List<User> =
        repo.findAll().map { it.toDomain() }
}