/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 * Permission granted for use, modification, and distribution with attribution.
 * No warranty provided.
 */

package com.zama.safeops.modules.auth.infrastructure.persistence.jpa.entities

import com.zama.safeops.modules.auth.domain.model.User
import com.zama.safeops.modules.auth.domain.valueobjects.Email
import com.zama.safeops.modules.auth.domain.valueobjects.PasswordHash
import com.zama.safeops.modules.auth.domain.valueobjects.UserId
import jakarta.persistence.*

@Entity
@Table(name = "app_user")
class UserJpaEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private val id: Long? = null,

    @Column(nullable = false, unique = true)
    private val email: String,

    @Column(name = "password_hash", nullable = false)
    private val passwordHash: String,

    @Column(nullable = false)
    private val enabled: Boolean = true,

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "user_role",
        joinColumns = [JoinColumn(name = "user_id")],
        inverseJoinColumns = [JoinColumn(name = "role_id")]
    )
    private val roles: Set<RoleJpaEntity> = emptySet()

) {

    fun toDomain(): User {
        requireNotNull(id) { "UserJpaEntity must be persisted before converting to domain" }
        return User(
            id = UserId(id),
            email = Email(email),
            password = PasswordHash(passwordHash),
            enabled = enabled,
            roles = roles.map { it.toDomain() }.toSet()
        )
    }

    companion object {
        fun fromDomain(user: User): UserJpaEntity =
            UserJpaEntity(
                id = user.id.value,
                email = user.email.value,
                passwordHash = user.password.value,
                enabled = user.enabled,
                roles = user.roles.map { RoleJpaEntity.fromDomain(it) }.toSet()
            )
    }
}