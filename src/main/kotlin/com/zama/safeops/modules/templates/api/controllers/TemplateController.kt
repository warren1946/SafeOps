/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 * Permission granted for use, modification, and distribution with attribution.
 * No warranty provided.
 */

package com.zama.safeops.modules.templates.api.controllers

import com.zama.safeops.modules.auth.infrastructure.rbac.RequiresRole
import com.zama.safeops.modules.shared.api.ApiController
import com.zama.safeops.modules.templates.api.dto.TemplateFilterCriteria
import com.zama.safeops.modules.templates.api.dto.TemplateRequest
import com.zama.safeops.modules.templates.api.mappers.toResponse
import com.zama.safeops.modules.templates.application.services.TemplateQueryService
import com.zama.safeops.modules.templates.application.services.TemplateQuestionService
import com.zama.safeops.modules.templates.application.services.TemplateService
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/templates", "/api/v1/templates")
class TemplateController(
    private val templateService: TemplateService,
    private val templateQuestionService: TemplateQuestionService,
    private val templateQueryService: TemplateQueryService
) : ApiController() {

    @RequiresRole("ADMIN", "OFFICER")
    @PostMapping
    fun create(@Valid @RequestBody req: TemplateRequest) = created(
        "Template created successfully",
        templateService.create(
            name = req.name,
            description = req.description,
            category = req.category,
            creatorId = currentUserId()
        ).let { template ->
            val questions = templateQuestionService.listByTemplate(template.id!!.value)
            template.toResponse(questions)
        }
    )

    @RequiresRole("ADMIN", "OFFICER", "VIEWER")
    @GetMapping
    fun list() = ok(
        "Templates retrieved successfully",
        templateService.list().map { template ->
            val questions = templateQuestionService.listByTemplate(template.id!!.value)
            template.toResponse(questions)
        }
    )

    @RequiresRole("ADMIN", "OFFICER", "VIEWER")
    @GetMapping("/{id}")
    fun get(@PathVariable id: Long) = ok(
        "Template retrieved successfully",
        templateService.get(id).let { template ->
            val questions = templateQuestionService.listByTemplate(id)
            template.toResponse(questions)
        }
    )

    @RequiresRole("ADMIN", "OFFICER")
    @PutMapping("/{id}")
    fun update(@PathVariable id: Long, @Valid @RequestBody req: TemplateRequest) = ok(
        "Template updated successfully",
        templateService.update(
            id = id,
            name = req.name,
            description = req.description,
            category = req.category
        ).let { template ->
            val questions = templateQuestionService.listByTemplate(id)
            template.toResponse(questions)
        }
    )

    @RequiresRole("ADMIN")
    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long) = ok(
        "Template deleted successfully",
        run { templateService.delete(id); null }
    )

    @RequiresRole("ADMIN", "OFFICER")
    @PostMapping("/{id}/copy")
    fun copy(@PathVariable id: Long) = created(
        "Template copied successfully",
        templateService.copy(id, currentUserId()).let { template ->
            val questions = templateQuestionService.listByTemplate(template.id!!.value)
            template.toResponse(questions)
        }
    )

    @RequiresRole("ADMIN", "OFFICER", "VIEWER")
    @PostMapping("/filter")
    fun filter(@RequestBody criteria: TemplateFilterCriteria) = ok(
        "Filtered templates",
        templateQueryService.getFiltered(criteria).map { template ->
            val questions = templateQuestionService.listByTemplate(template.id!!.value)
            template.toResponse(questions)
        }
    )

    @RequiresRole("ADMIN", "OFFICER")
    @PostMapping("/{id}/track-usage")
    fun trackUsage(@PathVariable id: Long) = ok(
        "Template usage tracked",
        run { templateService.trackUsage(id); null }
    )
}