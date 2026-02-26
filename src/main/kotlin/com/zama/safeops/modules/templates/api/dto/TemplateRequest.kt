/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 * Permission granted for use, modification, and distribution with attribution.
 * No warranty provided.
 */

package com.zama.safeops.modules.templates.api.dto

import com.zama.safeops.modules.shared.entities.SortDirection
import com.zama.safeops.modules.templates.domain.model.QuestionType

data class TemplateRequest(
    val name: String,
    val description: String,
    val category: String?
)

data class TemplateResponse(
    val id: Long,
    val name: String,
    val description: String,
    val category: String?,
    val usageCount: Int,
    val createdBy: Long?,
    val createdAt: String,
    val updatedAt: String,
    val questions: List<TemplateQuestionResponse>
)

data class TemplateQuestionRequest(
    val text: String,
    val type: QuestionType,
    val required: Boolean,
    val orderIndex: Int,
    val options: List<String> = emptyList()
)

data class TemplateQuestionResponse(
    val id: Long,
    val text: String,
    val type: QuestionType,
    val required: Boolean,
    val orderIndex: Int,
    val options: List<String>
)

data class TemplateFilterCriteria(
    val search: String? = null,
    val category: String? = null,
    val sortBy: TemplateSortField = TemplateSortField.NAME,
    val direction: SortDirection = SortDirection.ASC
)

enum class TemplateSortField {
    NAME,
    USAGE,
    CREATED_AT
}