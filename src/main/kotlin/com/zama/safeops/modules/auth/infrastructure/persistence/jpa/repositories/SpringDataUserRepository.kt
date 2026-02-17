/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 * Permission granted for use, modification, and distribution with attribution.
 * No warranty provided.
 */

package com.zama.safeops.modules.auth.infrastructure.persistence.jpa.repositories

import com.zama.safeops.modules.auth.infrastructure.persistence.jpa.entities.UserJpaEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface SpringDataUserRepository : JpaRepository<UserJpaEntity, Long> {
    fun findByEmail(email: String): Optional<UserJpaEntity>
    fun existsByEmail(email: String): Boolean
}