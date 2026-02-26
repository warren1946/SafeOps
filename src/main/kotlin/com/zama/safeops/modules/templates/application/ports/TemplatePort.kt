/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 * Permission granted for use, modification, and distribution with attribution.
 * No warranty provided.
 */

package com.zama.safeops.modules.templates.application.ports

import com.zama.safeops.modules.templates.domain.model.Template
import com.zama.safeops.modules.templates.domain.model.TemplateId

interface TemplatePort {
    fun create(template: Template): Template
    fun update(template: Template): Template
    fun delete(id: TemplateId)
    fun findById(id: TemplateId): Template?
    fun findAll(): List<Template>
    fun incrementUsage(id: TemplateId)
}