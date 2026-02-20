/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 * Permission granted for use, modification, and distribution with attribution.
 * No warranty provided.
 */

package com.zama.safeops.modules.safety.infrastructure.persistence.jpa.repositories

import com.zama.safeops.modules.safety.infrastructure.persistence.jpa.entities.SafetyReportJpaEntity
import org.springframework.data.jpa.repository.JpaRepository

interface SpringDataSafetyReportRepository : JpaRepository<SafetyReportJpaEntity, Long>