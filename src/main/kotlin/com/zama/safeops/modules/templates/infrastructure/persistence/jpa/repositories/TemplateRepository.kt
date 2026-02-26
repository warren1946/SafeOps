/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 * Permission granted for use, modification, and distribution with attribution.
 * No warranty provided.
 */

package com.zama.safeops.modules.templates.infrastructure.persistence.jpa.repositories

import com.zama.safeops.modules.templates.infrastructure.persistence.jpa.entities.TemplateJpaEntity
import org.springframework.data.jpa.repository.JpaRepository

interface TemplateRepository : JpaRepository<TemplateJpaEntity, Long>