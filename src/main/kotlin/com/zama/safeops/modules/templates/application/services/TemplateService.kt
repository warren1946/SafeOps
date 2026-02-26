/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 * Permission granted for use, modification, and distribution with attribution.
 * No warranty provided.
 */

package com.zama.safeops.modules.templates.application.services

import com.zama.safeops.modules.templates.application.ports.TemplatePort
import com.zama.safeops.modules.templates.application.ports.TemplateQuestionPort
import com.zama.safeops.modules.templates.domain.model.*
import org.springframework.stereotype.Service
import java.time.Instant

@Service
class TemplateService(
    private val templatePort: TemplatePort,
    private val questionPort: TemplateQuestionPort
) {

    fun create(name: String, description: String, category: String?, creatorId: Long): Template {
        validateName(name)
        validateDescription(description)

        val template = Template(
            name = TemplateName(name),
            description = TemplateDescription(description),
            category = category?.let { TemplateCategory(it) },
            createdBy = creatorId
        )

        return templatePort.create(template)
    }

    fun update(id: Long, name: String, description: String, category: String?): Template {
        validateName(name)
        validateDescription(description)

        val existing = get(id)
        val updated = existing.copy(
            name = TemplateName(name),
            description = TemplateDescription(description),
            category = category?.let { TemplateCategory(it) },
            updatedAt = Instant.now()
        )
        return templatePort.update(updated)
    }

    fun delete(id: Long) {
        templatePort.delete(TemplateId(id))
    }

    fun get(id: Long): Template =
        templatePort.findById(TemplateId(id)) ?: error("Template $id not found")

    fun list(): List<Template> =
        templatePort.findAll()

    fun copy(id: Long, creatorId: Long): Template {
        val original = get(id)
        val questions = questionPort.findByTemplate(TemplateId(id))

        val copy = templatePort.create(
            original.copy(
                id = null,
                name = TemplateName(original.name.value + " (Copy)"),
                usageCount = 0,
                createdBy = creatorId,
                createdAt = Instant.now(),
                updatedAt = Instant.now()
            )
        )

        questions.forEach { q ->
            questionPort.create(
                q.copy(
                    id = null,
                    templateId = copy.id!!
                )
            )
        }

        return copy
    }

    fun trackUsage(id: Long) =
        templatePort.incrementUsage(TemplateId(id))

    private fun validateName(name: String) {
        require(name.isNotBlank()) { "Template name cannot be blank" }
    }

    private fun validateDescription(description: String) {
        require(description.isNotBlank()) { "Template description cannot be blank" }
    }
}