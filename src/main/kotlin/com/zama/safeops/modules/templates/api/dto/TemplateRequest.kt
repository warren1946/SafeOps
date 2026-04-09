/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 * Permission granted for use, modification, and distribution with attribution.
 * No warranty provided.
 */

package com.zama.safeops.modules.templates.api.dto

import com.zama.safeops.config.validation.Sanitized
import com.zama.safeops.modules.shared.entities.SortDirection
import com.zama.safeops.modules.templates.domain.model.QuestionType
import jakarta.validation.constraints.*

private const val MAX_NAME_LENGTH = 200
private const val MAX_DESCRIPTION_LENGTH = 2000
private const val MAX_CATEGORY_LENGTH = 50
private const val MAX_QUESTION_TEXT_LENGTH = 500
private const val MAX_OPTIONS = 50
private const val MAX_SEARCH_LENGTH = 200

data class TemplateRequest(
    @field:NotBlank(message = "Name is required")
    @field:Size(max = MAX_NAME_LENGTH, message = "Name must not exceed $MAX_NAME_LENGTH characters")
    @field:Sanitized(maxLength = MAX_NAME_LENGTH, allowSpaces = true)
    val name: String,

    @field:Size(max = MAX_DESCRIPTION_LENGTH, message = "Description must not exceed $MAX_DESCRIPTION_LENGTH characters")
    @field:Sanitized(maxLength = MAX_DESCRIPTION_LENGTH, allowSpaces = true)
    val description: String,

    @field:Size(max = MAX_CATEGORY_LENGTH, message = "Category must not exceed $MAX_CATEGORY_LENGTH characters")
    @field:Sanitized(maxLength = MAX_CATEGORY_LENGTH)
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
    @field:NotBlank(message = "Question text is required")
    @field:Size(max = MAX_QUESTION_TEXT_LENGTH, message = "Question text must not exceed $MAX_QUESTION_TEXT_LENGTH characters")
    @field:Sanitized(maxLength = MAX_QUESTION_TEXT_LENGTH, allowSpaces = true)
    val text: String,

    @field:NotNull(message = "Question type is required")
    val type: QuestionType,

    val required: Boolean = false,

    @field:Min(0, message = "Order index must be non-negative")
    @field:Max(999, message = "Order index must be less than 1000")
    val orderIndex: Int = 0,

    @field:Size(max = MAX_OPTIONS, message = "Cannot have more than $MAX_OPTIONS options")
    val options: List<String> = emptyList()
) {
    init {
        // Validate option lengths
        require(options.all { it.length <= 200 }) { "Option text too long" }
    }
}

data class TemplateQuestionResponse(
    val id: Long,
    val text: String,
    val type: QuestionType,
    val required: Boolean,
    val orderIndex: Int,
    val options: List<String>
)

data class TemplateFilterCriteria(
    @field:Size(max = MAX_SEARCH_LENGTH, message = "Search term too long")
    @field:Sanitized(maxLength = MAX_SEARCH_LENGTH)
    val search: String? = null,

    @field:Size(max = MAX_CATEGORY_LENGTH, message = "Category too long")
    @field:Sanitized(maxLength = MAX_CATEGORY_LENGTH)
    val category: String? = null,

    val sortBy: TemplateSortField = TemplateSortField.NAME,
    val direction: SortDirection = SortDirection.ASC
)

enum class TemplateSortField {
    NAME,
    USAGE,
    CREATED_AT
}
