/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 * Permission granted for use, modification, and distribution with attribution.
 * No warranty provided.
 */

package com.zama.safeops.modules.hazards.infrastructure.persistence.repositories

import com.zama.safeops.modules.hazards.infrastructure.persistence.entities.HazardJpaEntity
import com.zama.safeops.modules.safety.domain.model.SafetyLocationType
import org.springframework.data.jpa.repository.JpaRepository

interface HazardRepository : JpaRepository<HazardJpaEntity, Long> {
    fun findByLocationTypeAndLocationId(locationType: SafetyLocationType, locationId: Long): List<HazardJpaEntity>
}