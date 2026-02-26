/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 * Permission granted for use, modification, and distribution with attribution.
 * No warranty provided.
 */

package com.zama.safeops.modules.templates.infrastructure.persistence.jpa.repositories

import com.zama.safeops.modules.templates.infrastructure.persistence.jpa.entities.TemplateQuestionJpaEntity
import org.springframework.data.jpa.repository.JpaRepository

interface TemplateQuestionRepository : JpaRepository<TemplateQuestionJpaEntity, Long> {
    fun findByTemplateId(templateId: Long): List<TemplateQuestionJpaEntity>
}