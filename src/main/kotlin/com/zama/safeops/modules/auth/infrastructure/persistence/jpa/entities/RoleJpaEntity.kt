/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 * Permission granted for use, modification, and distribution with attribution.
 * No warranty provided.
 */

package com.zama.safeops.modules.auth.infrastructure.persistence.jpa.entities

import com.zama.safeops.modules.auth.domain.model.Role
import com.zama.safeops.modules.auth.domain.valueobjects.RoleId
import com.zama.safeops.modules.auth.domain.valueobjects.RoleName
import jakarta.persistence.*

@Entity
@Table(name = "role")
class RoleJpaEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private val id: Long? = null,

    @Column(nullable = false, unique = true)
    private val name: String

) {

    fun toDomain(): Role {
        requireNotNull(id) { "RoleJpaEntity must be persisted before converting to domain" }
        return Role(
            id = RoleId(id),
            name = RoleName(name)
        )
    }

    companion object {
        fun fromDomain(role: Role): RoleJpaEntity =
            RoleJpaEntity(
                id = role.id?.value,
                name = role.name.value
            )
    }
}