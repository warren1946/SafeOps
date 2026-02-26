/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 * Permission granted for use, modification, and distribution with attribution.
 * No warranty provided.
 */

package com.zama.safeops.modules.templates.api.mappers

import com.zama.safeops.modules.templates.api.dto.TemplateQuestionResponse
import com.zama.safeops.modules.templates.api.dto.TemplateResponse
import com.zama.safeops.modules.templates.domain.model.Template
import com.zama.safeops.modules.templates.domain.model.TemplateQuestion

fun Template.toResponse(questions: List<TemplateQuestion>) = TemplateResponse(
    id = id?.value ?: error("Id can't be null"),
    name = name.value,
    description = description.value,
    category = category?.value,
    usageCount = usageCount,
    createdBy = createdBy,
    createdAt = createdAt.toString(),
    updatedAt = updatedAt.toString(),
    questions = questions.map { it.toResponse() }
)

fun TemplateQuestion.toResponse() = TemplateQuestionResponse(
    id = id?.value ?: error("Id can't be null"),
    text = text.value,
    type = type,
    required = required,
    orderIndex = orderIndex,
    options = options
)