/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 * Permission granted for use, modification, and distribution with attribution.
 * No warranty provided.
 */

package com.zama.safeops.modules.auth.infrastructure.persistence.jpa.adapters

import com.zama.safeops.modules.auth.application.ports.RolePort
import com.zama.safeops.modules.auth.domain.model.Role
import com.zama.safeops.modules.auth.domain.valueobjects.RoleId
import com.zama.safeops.modules.auth.domain.valueobjects.RoleName
import com.zama.safeops.modules.auth.infrastructure.persistence.jpa.entities.RoleJpaEntity
import com.zama.safeops.modules.auth.infrastructure.persistence.jpa.repositories.SpringDataRoleRepository
import org.springframework.stereotype.Component

@Component
class RoleJpaAdapter(
    private val repo: SpringDataRoleRepository
) : RolePort {

    override fun save(role: Role): Role =
        repo.save(RoleJpaEntity.fromDomain(role)).toDomain()

    override fun findByName(name: RoleName): Role? =
        repo.findByName(name.value).orElse(null)?.toDomain()

    override fun findById(id: RoleId): Role? =
        repo.findById(id.value).orElse(null)?.toDomain()

    override fun findAll(): List<Role> =
        repo.findAll().map { it.toDomain() }
}