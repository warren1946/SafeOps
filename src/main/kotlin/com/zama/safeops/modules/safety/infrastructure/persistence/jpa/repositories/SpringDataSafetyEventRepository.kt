/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 * Permission granted for use, modification, and distribution with attribution.
 * No warranty provided.
 */

package com.zama.safeops.modules.safety.infrastructure.persistence.jpa.repositories

import com.zama.safeops.modules.safety.infrastructure.persistence.jpa.entities.SafetyEventJpaEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.time.Instant

interface SpringDataSafetyEventRepository : JpaRepository<SafetyEventJpaEntity, Long> {

    fun findByCreatedAtBetween(start: Instant, end: Instant): List<SafetyEventJpaEntity>

    @Query(
        """
        select e.severity as key, count(e) as value
        from SafetyEventJpaEntity e
        group by e.severity
        """
    )
    fun countBySeverityRaw(): List<Array<Any>>

    @Query(
        """
        select e.type as key, count(e) as value
        from SafetyEventJpaEntity e
        group by e.type
        """
    )
    fun countByTypeRaw(): List<Array<Any>>

    fun findTop50ByOrderByCreatedAtDesc(): List<SafetyEventJpaEntity>
}