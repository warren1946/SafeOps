/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 * Permission granted for use, modification, and distribution with attribution.
 * No warranty provided.
 */

package com.zama.safeops.modules.templates.application.services

import com.zama.safeops.modules.templates.application.ports.TemplateQuestionPort
import com.zama.safeops.modules.templates.domain.model.*
import org.springframework.stereotype.Service

@Service
class TemplateQuestionService(
    private val questionPort: TemplateQuestionPort
) {

    fun addQuestion(
        templateId: Long,
        text: String,
        type: QuestionType,
        required: Boolean,
        orderIndex: Int,
        options: List<String>
    ): TemplateQuestion =
        questionPort.create(
            TemplateQuestion(
                templateId = TemplateId(templateId),
                text = QuestionText(text),
                type = type,
                required = required,
                orderIndex = orderIndex,
                options = options
            )
        )

    fun updateQuestion(
        questionId: Long,
        templateId: Long,
        text: String,
        type: QuestionType,
        required: Boolean,
        orderIndex: Int,
        options: List<String>
    ): TemplateQuestion =
        questionPort.update(
            TemplateQuestion(
                id = TemplateQuestionId(questionId),
                templateId = TemplateId(templateId),
                text = QuestionText(text),
                type = type,
                required = required,
                orderIndex = orderIndex,
                options = options
            )
        )

    fun deleteQuestion(questionId: Long) =
        questionPort.delete(TemplateQuestionId(questionId))

    fun listByTemplate(templateId: Long): List<TemplateQuestion> =
        questionPort.findByTemplate(TemplateId(templateId))
}