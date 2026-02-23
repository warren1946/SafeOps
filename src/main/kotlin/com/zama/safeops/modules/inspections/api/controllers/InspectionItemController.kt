/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 * Permission granted for use, modification, and distribution with attribution.
 * No warranty provided.
 */

package com.zama.safeops.modules.inspections.api.controllers

import com.zama.safeops.modules.auth.infrastructure.rbac.RequiresRole
import com.zama.safeops.modules.inspections.api.dto.AddInspectionItemRequest
import com.zama.safeops.modules.inspections.api.mappers.toResponse
import com.zama.safeops.modules.inspections.application.ports.InspectionItemPort
import com.zama.safeops.modules.inspections.domain.model.InspectionItem
import com.zama.safeops.modules.inspections.domain.valueobjects.InspectionId
import com.zama.safeops.modules.shared.api.ApiController
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/inspections/{inspectionId}/items")
class InspectionItemController(
    private val itemPort: InspectionItemPort
) : ApiController() {

    @RequiresRole("ADMIN", "OFFICER")
    @GetMapping
    fun list(@PathVariable inspectionId: Long) = ok(
        "Inspection items",
        itemPort.findByInspectionId(InspectionId(inspectionId)).map { it.toResponse() }
    )

    @RequiresRole("ADMIN", "OFFICER")
    @PostMapping
    fun add(
        @PathVariable inspectionId: Long,
        @RequestBody req: AddInspectionItemRequest
    ) = ok(
        "Item added",
        itemPort.create(
            InspectionItem(
                id = null,
                inspectionId = InspectionId(inspectionId),
                title = req.title,
                status = req.status,
                comment = req.comment
            )
        ).toResponse()
    )
}