/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 * Permission granted for use, modification, and distribution with attribution.
 * No warranty provided.
 */

package com.zama.safeops.modules.templates.infrastructure.persistence.jpa.adapters

import com.zama.safeops.modules.templates.application.ports.TemplatePort
import com.zama.safeops.modules.templates.domain.model.*
import com.zama.safeops.modules.templates.infrastructure.persistence.jpa.entities.TemplateJpaEntity
import com.zama.safeops.modules.templates.infrastructure.persistence.jpa.repositories.TemplateRepository
import org.springframework.stereotype.Component
import java.time.Instant

@Component
class TemplatePersistenceAdapter(private val repo: TemplateRepository) : TemplatePort {

    override fun create(template: Template): Template = repo.save(template.toEntity()).toDomain()
    override fun update(template: Template): Template = repo.save(template.toEntity()).toDomain()
    override fun delete(id: TemplateId) = repo.deleteById(id.value)
    override fun findById(id: TemplateId): Template? = repo.findById(id.value).orElse(null)?.toDomain()
    override fun findAll(): List<Template> = repo.findAll().map { it.toDomain() }
    override fun incrementUsage(id: TemplateId) {
        val entity = repo.findById(id.value).orElse(null) ?: return
        val updated = TemplateJpaEntity(
            id = entity.id,
            name = entity.name,
            description = entity.description,
            category = entity.category,
            createdBy = entity.createdBy,
            usageCount = entity.usageCount + 1,
            createdAt = entity.createdAt,
            updatedAt = Instant.now()
        )
        repo.save(updated)
    }
}

private fun Template.toEntity() = TemplateJpaEntity(
    id = id?.value,
    name = name.value,
    description = description.value,
    category = category?.value,
    createdBy = createdBy,
    usageCount = usageCount,
    createdAt = createdAt,
    updatedAt = updatedAt
)

private fun TemplateJpaEntity.toDomain() = Template(
    id = id?.let { TemplateId(it) },
    name = TemplateName(name),
    description = TemplateDescription(description),
    category = category?.let { TemplateCategory(it) },
    createdBy = createdBy,
    usageCount = usageCount,
    createdAt = createdAt,
    updatedAt = updatedAt
)