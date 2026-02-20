/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 * Permission granted for use, modification, and distribution with attribution.
 * No warranty provided.
 */

package com.zama.safeops.modules.inspections.application.services

import com.zama.safeops.modules.auth.application.ports.UserPort
import com.zama.safeops.modules.auth.domain.valueobjects.UserId
import com.zama.safeops.modules.core.application.ports.AreaPort
import com.zama.safeops.modules.core.application.ports.ShaftPort
import com.zama.safeops.modules.core.application.ports.SitePort
import com.zama.safeops.modules.core.domain.valueobjects.AreaId
import com.zama.safeops.modules.core.domain.valueobjects.ShaftId
import com.zama.safeops.modules.core.domain.valueobjects.SiteId
import com.zama.safeops.modules.inspections.api.dto.AddInspectionItemRequest
import com.zama.safeops.modules.inspections.api.dto.CreateInspectionRequest
import com.zama.safeops.modules.inspections.application.exceptions.InspectionInvalidInputException
import com.zama.safeops.modules.inspections.application.exceptions.InspectionInvalidStateException
import com.zama.safeops.modules.inspections.application.exceptions.InspectionNotFoundException
import com.zama.safeops.modules.inspections.application.ports.InspectionItemPort
import com.zama.safeops.modules.inspections.application.ports.InspectionPort
import com.zama.safeops.modules.inspections.domain.model.Inspection
import com.zama.safeops.modules.inspections.domain.model.InspectionItem
import com.zama.safeops.modules.inspections.domain.model.InspectionStatus
import com.zama.safeops.modules.inspections.domain.model.InspectionTargetType
import com.zama.safeops.modules.inspections.domain.valueobjects.InspectionId
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant

@Service
class InspectionService(
    private val inspectionPort: InspectionPort,
    private val inspectionItemPort: InspectionItemPort,
    private val areaPort: AreaPort,
    private val shaftPort: ShaftPort,
    private val sitePort: SitePort,
    private val userPort: UserPort
) {

    @Transactional
    fun create(req: CreateInspectionRequest): Inspection {
        validateCreate(req)
        validateTarget(req.targetType, req.targetId)
        validateInspector(req.inspectorId)

        val inspection = Inspection(
            title = req.title,
            targetType = req.targetType,
            targetId = req.targetId,
            inspectorId = req.inspectorId
        )

        return inspectionPort.create(inspection)
    }

    @Transactional
    fun addItem(inspectionId: Long, req: AddInspectionItemRequest): InspectionItem {
        val inspection = getInspection(inspectionId)

        if (inspection.status != InspectionStatus.DRAFT) {
            throw InspectionInvalidStateException("Cannot add items to inspection in status ${inspection.status}")
        }

        val item = InspectionItem(
            inspectionId = InspectionId(inspectionId),
            title = req.title,
            status = req.status,
            comment = req.comment
        )
        return inspectionItemPort.create(item)
    }

    @Transactional
    fun submit(inspectionId: Long): Inspection {
        val inspection = getInspection(inspectionId)
        validateTarget(inspection.targetType, inspection.targetId)
        validateInspector(inspection.inspectorId)

        if (inspection.status != InspectionStatus.DRAFT) {
            throw InspectionInvalidStateException("Only DRAFT inspections can be submitted")
        }

        val items = inspectionItemPort.findByInspectionId(InspectionId(inspectionId))
        if (items.isEmpty()) {
            throw InspectionInvalidInputException("Cannot submit an inspection without items")
        }

        val updated = inspection.copy(
            status = InspectionStatus.SUBMITTED,
            updatedAt = Instant.now()
        )
        return inspectionPort.update(updated)
    }

    @Transactional
    fun approve(inspectionId: Long, reviewerComments: String?): Inspection {
        val inspection = getInspection(inspectionId)

        if (inspection.status != InspectionStatus.SUBMITTED) {
            throw InspectionInvalidStateException("Only SUBMITTED inspections can be approved")
        }

        if (inspection.assignedReviewerId == null) {
            throw InspectionInvalidStateException("Inspection must have an assigned reviewer before approval")
        }

        val updated = inspection.copy(
            status = InspectionStatus.APPROVED,
            reviewerComments = reviewerComments,
            updatedAt = Instant.now()
        )

        return inspectionPort.update(updated)
    }

    @Transactional
    fun reject(inspectionId: Long, reviewerComments: String?): Inspection {
        val inspection = getInspection(inspectionId)

        if (inspection.status != InspectionStatus.SUBMITTED) {
            throw InspectionInvalidStateException("Only SUBMITTED inspections can be rejected")
        }

        if (inspection.assignedReviewerId == null) {
            throw InspectionInvalidStateException("Inspection must have an assigned reviewer before rejection")
        }

        val updated = inspection.copy(
            status = InspectionStatus.REJECTED,
            reviewerComments = reviewerComments,
            updatedAt = Instant.now()
        )

        return inspectionPort.update(updated)
    }

    @Transactional(readOnly = true)
    fun list(): List<Inspection> =
        inspectionPort.findAll()

    @Transactional(readOnly = true)
    fun getWithItems(id: Long): Pair<Inspection, List<InspectionItem>> {
        val inspection = getInspection(id)
        val items = inspectionItemPort.findByInspectionId(InspectionId(id))
        return inspection to items
    }

    private fun getInspection(id: Long): Inspection =
        inspectionPort.findById(InspectionId(id)) ?: throw InspectionNotFoundException(id)

    @Transactional
    fun assignReviewer(inspectionId: Long, reviewerId: Long): Inspection {
        val inspection = getInspection(inspectionId)

        if (inspection.status !in listOf(InspectionStatus.DRAFT, InspectionStatus.SUBMITTED)) {
            throw InspectionInvalidStateException(
                "Reviewer can only be assigned in DRAFT or SUBMITTED status"
            )
        }

        validateReviewer(reviewerId)

        if (reviewerId == inspection.inspectorId) {
            throw InspectionInvalidInputException("Inspector cannot be the reviewer")
        }

        val updated = inspection.copy(
            assignedReviewerId = reviewerId,
            updatedAt = Instant.now()
        )

        return inspectionPort.update(updated)
    }

    private fun validateCreate(req: CreateInspectionRequest) {
        if (req.title.isBlank()) {
            throw InspectionInvalidInputException("Title cannot be blank")
        }
        if (req.targetId <= 0) {
            throw InspectionInvalidInputException("Invalid targetId: ${req.targetId}")
        }
        if (req.inspectorId <= 0) {
            throw InspectionInvalidInputException("Invalid inspectorId: ${req.inspectorId}")
        }
    }

    private fun validateTarget(targetType: InspectionTargetType, targetId: Long) {
        if (targetId <= 0) {
            throw InspectionInvalidInputException("Invalid targetId: $targetId")
        }

        val exists = when (targetType) {
            InspectionTargetType.AREA ->
                areaPort.exists(AreaId(targetId))

            InspectionTargetType.SHAFT ->
                shaftPort.exists(ShaftId(targetId))

            InspectionTargetType.SITE ->
                sitePort.exists(SiteId(targetId))
        }

        if (!exists) {
            throw InspectionInvalidInputException(
                "Target ${targetType.name} with ID $targetId does not exist"
            )
        }
    }

    private fun validateInspector(inspectorId: Long) {
        if (inspectorId <= 0) {
            throw InspectionInvalidInputException("Invalid inspectorId: $inspectorId")
        }

        if (!userPort.existsById(UserId(inspectorId))) {
            throw InspectionInvalidInputException("Inspector with ID $inspectorId does not exist")
        }
    }

    private fun validateReviewer(reviewerId: Long) {
        if (reviewerId <= 0) {
            throw InspectionInvalidInputException("Invalid reviewerId: $reviewerId")
        }

        if (!userPort.existsById(UserId(reviewerId))) {
            throw InspectionInvalidInputException("Reviewer with ID $reviewerId does not exist")
        }
    }
}