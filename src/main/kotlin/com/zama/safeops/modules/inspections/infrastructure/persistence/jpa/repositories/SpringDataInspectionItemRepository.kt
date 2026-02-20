/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 * Permission granted for use, modification, and distribution with attribution.
 * No warranty provided.
 */

package com.zama.safeops.modules.inspections.infrastructure.persistence.jpa.repositories

import com.zama.safeops.modules.inspections.infrastructure.persistence.jpa.entities.InspectionItemJpaEntity
import org.springframework.data.jpa.repository.JpaRepository

interface SpringDataInspectionItemRepository : JpaRepository<InspectionItemJpaEntity, Long> {
    fun findByInspectionId(inspectionId: Long): List<InspectionItemJpaEntity>
}