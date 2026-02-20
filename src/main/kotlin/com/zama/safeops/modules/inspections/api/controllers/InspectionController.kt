/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 * Permission granted for use, modification, and distribution with attribution.
 * No warranty provided.
 */

package com.zama.safeops.modules.inspections.api.controllers

import com.zama.safeops.modules.inspections.api.dto.*
import com.zama.safeops.modules.inspections.api.mappers.toResponse
import com.zama.safeops.modules.inspections.application.services.InspectionService
import com.zama.safeops.modules.shared.api.ApiController
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/inspections")
class InspectionController(private val inspectionService: InspectionService) : ApiController() {

    @PostMapping
    fun create(@Valid @RequestBody req: CreateInspectionRequest) = created(
        "Inspection created successfully",
        inspectionService.getWithItems(
            inspectionService.create(req).id?.value
                ?: error("Inspection ID must not be null after creation")
        ).let { (inspection, items) ->
            inspection.toResponse(items)
        }
    )

    @PostMapping("/{id}/items")
    fun addItem(@PathVariable id: Long, @Valid @RequestBody req: AddInspectionItemRequest) = created(
        "Inspection item added successfully",
        inspectionService.addItem(id, req).toResponse()
    )

    @PostMapping("/{id}/assign-reviewer")
    fun assignReviewer(@PathVariable id: Long, @Valid @RequestBody req: AssignReviewerRequest) = ok(
        "Reviewer assigned successfully",
        inspectionService.getWithItems(
            inspectionService.assignReviewer(id, req.reviewerId).id?.value
                ?: error("Inspection ID must not be null after reviewer assignment")
        ).let { (inspection, items) ->
            inspection.toResponse(items)
        }
    )

    @PostMapping("/{id}/submit")
    fun submit(@PathVariable id: Long) = ok(
        "Inspection submitted successfully",
        inspectionService.getWithItems(
            inspectionService.submit(id).id?.value
                ?: error("Inspection ID must not be null after submission")
        ).let { (inspection, items) ->
            inspection.toResponse(items)
        }
    )

    @PostMapping("/{id}/approve")
    fun approve(@PathVariable id: Long, @RequestBody req: ApproveInspectionRequest) = ok(
        "Inspection approved successfully",
        inspectionService.getWithItems(
            inspectionService.approve(id, req.reviewerComments).id?.value
                ?: error("Inspection ID must not be null after approval")
        ).let { (inspection, items) ->
            inspection.toResponse(items)
        }
    )

    @PostMapping("/{id}/reject")
    fun reject(@PathVariable id: Long, @RequestBody req: RejectInspectionRequest) = ok(
        "Inspection rejected successfully",
        inspectionService.getWithItems(
            inspectionService.reject(id, req.reviewerComments).id?.value
                ?: error("Inspection ID must not be null after rejection")
        ).let { (inspection, items) ->
            inspection.toResponse(items)
        }
    )

    @GetMapping
    fun list() = ok(
        "Inspections retrieved successfully",
        inspectionService.list().map { inspection ->
            inspection.toResponse(emptyList())
        }
    )

    @GetMapping("/{id}")
    fun get(@PathVariable id: Long) = ok(
        "Inspection retrieved successfully",
        inspectionService.getWithItems(id).let { (inspection, items) ->
            inspection.toResponse(items)
        }
    )
}