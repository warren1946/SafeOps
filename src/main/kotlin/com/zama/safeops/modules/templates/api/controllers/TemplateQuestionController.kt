/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 * Permission granted for use, modification, and distribution with attribution.
 * No warranty provided.
 */

package com.zama.safeops.modules.templates.api.controllers

import com.zama.safeops.modules.auth.infrastructure.rbac.RequiresRole
import com.zama.safeops.modules.shared.api.ApiController
import com.zama.safeops.modules.templates.api.dto.TemplateQuestionRequest
import com.zama.safeops.modules.templates.api.mappers.toResponse
import com.zama.safeops.modules.templates.application.services.TemplateQuestionService
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/templates/{templateId}/questions")
class TemplateQuestionController(
    private val templateQuestionService: TemplateQuestionService
) : ApiController() {

    @RequiresRole("ADMIN", "OFFICER")
    @PostMapping
    fun add(
        @PathVariable templateId: Long,
        @Valid @RequestBody req: TemplateQuestionRequest
    ) = created(
        "Question added successfully",
        templateQuestionService.addQuestion(
            templateId = templateId,
            text = req.text,
            type = req.type,
            required = req.required,
            orderIndex = req.orderIndex,
            options = req.options
        ).toResponse()
    )

    @RequiresRole("ADMIN", "OFFICER")
    @PutMapping("/{questionId}")
    fun update(
        @PathVariable templateId: Long,
        @PathVariable questionId: Long,
        @Valid @RequestBody req: TemplateQuestionRequest
    ) = ok(
        "Question updated successfully",
        templateQuestionService.updateQuestion(
            questionId = questionId,
            templateId = templateId,
            text = req.text,
            type = req.type,
            required = req.required,
            orderIndex = req.orderIndex,
            options = req.options
        ).toResponse()
    )

    @RequiresRole("ADMIN", "OFFICER")
    @DeleteMapping("/{questionId}")
    fun delete(
        @PathVariable questionId: Long
    ) = ok(
        "Question deleted successfully",
        run { templateQuestionService.deleteQuestion(questionId); null }
    )
}