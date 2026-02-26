/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 * Permission granted for use, modification, and distribution with attribution.
 * No warranty provided.
 */

package com.zama.safeops.modules.templates.domain.model

data class TemplateQuestion(
    val id: TemplateQuestionId? = null,
    val templateId: TemplateId,
    val text: QuestionText,
    val type: QuestionType,
    val required: Boolean,
    val orderIndex: Int,
    val options: List<String> = emptyList()
)

@JvmInline
value class TemplateQuestionId(val value: Long)

@JvmInline
value class QuestionText(val value: String)

enum class QuestionType {
    TEXT,
    NUMBER,
    BOOLEAN,
    MULTI_CHOICE
}