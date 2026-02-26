/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 * Permission granted for use, modification, and distribution with attribution.
 * No warranty provided.
 */

package com.zama.safeops.modules.templates.infrastructure.persistence.jpa.adapters

import com.zama.safeops.modules.templates.application.ports.TemplateQuestionPort
import com.zama.safeops.modules.templates.domain.model.QuestionText
import com.zama.safeops.modules.templates.domain.model.TemplateId
import com.zama.safeops.modules.templates.domain.model.TemplateQuestion
import com.zama.safeops.modules.templates.domain.model.TemplateQuestionId
import com.zama.safeops.modules.templates.infrastructure.persistence.jpa.entities.TemplateQuestionJpaEntity
import com.zama.safeops.modules.templates.infrastructure.persistence.jpa.repositories.TemplateQuestionRepository
import org.springframework.stereotype.Component
import tools.jackson.module.kotlin.jacksonObjectMapper
import tools.jackson.module.kotlin.readValue

@Component
class TemplateQuestionPersistenceAdapter(
    private val repo: TemplateQuestionRepository
) : TemplateQuestionPort {

    private val mapper = jacksonObjectMapper()

    override fun create(question: TemplateQuestion): TemplateQuestion =
        repo.save(question.toEntity()).toDomain()

    override fun update(question: TemplateQuestion): TemplateQuestion =
        repo.save(question.toEntity()).toDomain()

    override fun delete(id: TemplateQuestionId) =
        repo.deleteById(id.value)

    override fun findByTemplate(templateId: TemplateId): List<TemplateQuestion> =
        repo.findByTemplateId(templateId.value).map { it.toDomain() }

    private fun TemplateQuestion.toEntity() = TemplateQuestionJpaEntity(
        id = id?.value,
        templateId = templateId.value,
        text = text.value,
        type = type,
        required = required,
        orderIndex = orderIndex,
        options = if (options.isEmpty()) null else mapper.writeValueAsString(options)
    )

    private fun TemplateQuestionJpaEntity.toDomain(): TemplateQuestion =
        TemplateQuestion(
            id = id?.let { TemplateQuestionId(it) },
            templateId = TemplateId(templateId),
            text = QuestionText(text),
            type = type,
            required = required,
            orderIndex = orderIndex,
            options = options?.let { mapper.readValue<List<String>>(it) } ?: emptyList()
        )
}