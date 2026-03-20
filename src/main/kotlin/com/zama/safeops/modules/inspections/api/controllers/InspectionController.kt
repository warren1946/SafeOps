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
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import com.zama.safeops.modules.shared.api.ApiResponse as SafeOpsApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponse as SwaggerApiResponse

@RestController
@RequestMapping("/api/v1/inspections")
@Tag(name = "Inspections", description = "Safety inspection management endpoints")
@SecurityRequirement(name = "bearerAuth")
class InspectionController(private val inspectionService: InspectionService) : ApiController() {

    @Operation(
        summary = "Create a new inspection",
        description = "Creates a new safety inspection with the specified template and target location"
    )
    @ApiResponses(
        value = [
            SwaggerApiResponse(
                responseCode = "201",
                description = "Inspection created successfully",
                content = [Content(schema = Schema(implementation = SafeOpsApiResponse::class))]
            ),
            SwaggerApiResponse(
                responseCode = "400",
                description = "Invalid input data",
                content = [Content(schema = Schema(implementation = SafeOpsApiResponse::class))]
            ),
            SwaggerApiResponse(
                responseCode = "401",
                description = "Unauthorized - valid JWT token required",
                content = [Content(schema = Schema(implementation = SafeOpsApiResponse::class))]
            ),
            SwaggerApiResponse(
                responseCode = "404",
                description = "Template or target location not found",
                content = [Content(schema = Schema(implementation = SafeOpsApiResponse::class))]
            )
        ]
    )
    @PostMapping
    fun create(
        @Valid @RequestBody req: CreateInspectionRequest
    ): ResponseEntity<SafeOpsApiResponse<InspectionResponse>> = created(
        "Inspection created successfully",
        inspectionService.getWithItems(
            inspectionService.create(req).id?.value
                ?: error("Inspection ID must not be null after creation")
        ).let { (inspection, items) ->
            inspection.toResponse(items)
        }
    )

    @Operation(
        summary = "Add item to inspection",
        description = "Adds a question/answer item to an existing inspection"
    )
    @ApiResponses(
        value = [
            SwaggerApiResponse(responseCode = "201", description = "Item added successfully"),
            SwaggerApiResponse(responseCode = "400", description = "Invalid input or inspection not in DRAFT status"),
            SwaggerApiResponse(responseCode = "404", description = "Inspection not found")
        ]
    )
    @PostMapping("/{id}/items")
    fun addItem(
        @Parameter(description = "Inspection ID", required = true, example = "123")
        @PathVariable id: Long,
        @Valid @RequestBody req: AddInspectionItemRequest
    ): ResponseEntity<SafeOpsApiResponse<InspectionItemResponse>> = created(
        "Inspection item added successfully",
        inspectionService.addItem(id, req).toResponse()
    )

    @Operation(
        summary = "Assign reviewer to inspection",
        description = "Assigns a reviewer to an inspection for approval workflow"
    )
    @ApiResponses(
        value = [
            SwaggerApiResponse(responseCode = "200", description = "Reviewer assigned successfully"),
            SwaggerApiResponse(responseCode = "400", description = "Invalid reviewer or inspection state"),
            SwaggerApiResponse(responseCode = "404", description = "Inspection or reviewer not found")
        ]
    )
    @PostMapping("/{id}/assign-reviewer")
    fun assignReviewer(
        @Parameter(description = "Inspection ID", required = true)
        @PathVariable id: Long,
        @Valid @RequestBody req: AssignReviewerRequest
    ): ResponseEntity<SafeOpsApiResponse<InspectionResponse>> = ok(
        "Reviewer assigned successfully",
        inspectionService.getWithItems(
            inspectionService.assignReviewer(id, req.reviewerId).id?.value
                ?: error("Inspection ID must not be null after reviewer assignment")
        ).let { (inspection, items) ->
            inspection.toResponse(items)
        }
    )

    @Operation(
        summary = "Submit inspection",
        description = "Submits an inspection for review. Inspection must be in DRAFT status and have at least one item."
    )
    @ApiResponses(
        value = [
            SwaggerApiResponse(responseCode = "200", description = "Inspection submitted successfully"),
            SwaggerApiResponse(responseCode = "400", description = "Inspection not in DRAFT status or has no items"),
            SwaggerApiResponse(responseCode = "404", description = "Inspection not found")
        ]
    )
    @PostMapping("/{id}/submit")
    fun submit(
        @Parameter(description = "Inspection ID", required = true)
        @PathVariable id: Long
    ): ResponseEntity<SafeOpsApiResponse<InspectionResponse>> = ok(
        "Inspection submitted successfully",
        inspectionService.getWithItems(
            inspectionService.submit(id).id?.value
                ?: error("Inspection ID must not be null after submission")
        ).let { (inspection, items) ->
            inspection.toResponse(items)
        }
    )

    @Operation(
        summary = "Approve inspection",
        description = "Approves a submitted inspection. Requires reviewer to be assigned."
    )
    @ApiResponses(
        value = [
            SwaggerApiResponse(responseCode = "200", description = "Inspection approved successfully"),
            SwaggerApiResponse(responseCode = "400", description = "Inspection not in SUBMITTED status or no reviewer assigned"),
            SwaggerApiResponse(responseCode = "404", description = "Inspection not found")
        ]
    )
    @PostMapping("/{id}/approve")
    fun approve(
        @Parameter(description = "Inspection ID", required = true)
        @PathVariable id: Long,
        @RequestBody req: ApproveInspectionRequest
    ): ResponseEntity<SafeOpsApiResponse<InspectionResponse>> = ok(
        "Inspection approved successfully",
        inspectionService.getWithItems(
            inspectionService.approve(id, req.reviewerComments).id?.value
                ?: error("Inspection ID must not be null after approval")
        ).let { (inspection, items) ->
            inspection.toResponse(items)
        }
    )

    @Operation(
        summary = "Reject inspection",
        description = "Rejects a submitted inspection with optional comments"
    )
    @ApiResponses(
        value = [
            SwaggerApiResponse(responseCode = "200", description = "Inspection rejected successfully"),
            SwaggerApiResponse(responseCode = "400", description = "Inspection not in SUBMITTED status"),
            SwaggerApiResponse(responseCode = "404", description = "Inspection not found")
        ]
    )
    @PostMapping("/{id}/reject")
    fun reject(
        @Parameter(description = "Inspection ID", required = true)
        @PathVariable id: Long,
        @RequestBody req: RejectInspectionRequest
    ): ResponseEntity<SafeOpsApiResponse<InspectionResponse>> = ok(
        "Inspection rejected successfully",
        inspectionService.getWithItems(
            inspectionService.reject(id, req.reviewerComments).id?.value
                ?: error("Inspection ID must not be null after rejection")
        ).let { (inspection, items) ->
            inspection.toResponse(items)
        }
    )

    @Operation(
        summary = "List all inspections",
        description = "Retrieves a list of all inspections for the current tenant"
    )
    @ApiResponses(
        value = [
            SwaggerApiResponse(responseCode = "200", description = "Inspections retrieved successfully")
        ]
    )
    @GetMapping
    fun list(): ResponseEntity<SafeOpsApiResponse<List<InspectionResponse>>> = ok(
        "Inspections retrieved successfully",
        inspectionService.list().map { inspection ->
            inspection.toResponse(emptyList())
        }
    )

    @Operation(
        summary = "Get inspection by ID",
        description = "Retrieves detailed information about a specific inspection including all items"
    )
    @ApiResponses(
        value = [
            SwaggerApiResponse(responseCode = "200", description = "Inspection retrieved successfully"),
            SwaggerApiResponse(responseCode = "404", description = "Inspection not found")
        ]
    )
    @GetMapping("/{id}")
    fun get(
        @Parameter(description = "Inspection ID", required = true)
        @PathVariable id: Long
    ): ResponseEntity<SafeOpsApiResponse<InspectionResponse>> = ok(
        "Inspection retrieved successfully",
        inspectionService.getWithItems(id).let { (inspection, items) ->
            inspection.toResponse(items)
        }
    )
}
