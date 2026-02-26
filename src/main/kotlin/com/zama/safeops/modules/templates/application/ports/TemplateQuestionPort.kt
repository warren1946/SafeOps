/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 * Permission granted for use, modification, and distribution with attribution.
 * No warranty provided.
 */

package com.zama.safeops.modules.templates.application.ports

import com.zama.safeops.modules.templates.domain.model.TemplateId
import com.zama.safeops.modules.templates.domain.model.TemplateQuestion
import com.zama.safeops.modules.templates.domain.model.TemplateQuestionId

interface TemplateQuestionPort {
    fun create(question: TemplateQuestion): TemplateQuestion
    fun update(question: TemplateQuestion): TemplateQuestion
    fun delete(id: TemplateQuestionId)
    fun findByTemplate(templateId: TemplateId): List<TemplateQuestion>
}