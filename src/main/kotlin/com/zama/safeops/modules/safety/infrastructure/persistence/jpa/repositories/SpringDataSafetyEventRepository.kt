/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 * Permission granted for use, modification, and distribution with attribution.
 * No warranty provided.
 */

package com.zama.safeops.modules.safety.infrastructure.persistence.jpa.repositories

import com.zama.safeops.modules.safety.infrastructure.persistence.jpa.entities.SafetyEventJpaEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.time.Instant

interface SpringDataSafetyEventRepository : JpaRepository<SafetyEventJpaEntity, Long> {
    fun findByCreatedAtBetween(start: Instant, end: Instant): List<SafetyEventJpaEntity>
}