/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 * Permission granted for use, modification, and distribution with attribution.
 * No warranty provided.
 */

package com.zama.safeops.modules.inspections.infrastructure.persistence.jpa.repositories

import com.zama.safeops.modules.inspections.infrastructure.persistence.jpa.entities.InspectionJpaEntity
import org.springframework.data.jpa.repository.JpaRepository

interface SpringDataInspectionRepository : JpaRepository<InspectionJpaEntity, Long>