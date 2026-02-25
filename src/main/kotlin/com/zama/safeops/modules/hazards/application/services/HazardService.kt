/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 * Permission granted for use, modification, and distribution with attribution.
 * No warranty provided.
 */

package com.zama.safeops.modules.hazards.application.services

import com.zama.safeops.modules.auth.application.ports.UserPort
import com.zama.safeops.modules.auth.domain.valueobjects.UserId
import com.zama.safeops.modules.core.application.ports.AreaPort
import com.zama.safeops.modules.core.application.ports.ShaftPort
import com.zama.safeops.modules.core.application.ports.SitePort
import com.zama.safeops.modules.core.domain.valueobjects.AreaId
import com.zama.safeops.modules.core.domain.valueobjects.ShaftId
import com.zama.safeops.modules.core.domain.valueobjects.SiteId
import com.zama.safeops.modules.hazards.api.dto.CreateHazardRequest
import com.zama.safeops.modules.hazards.application.ports.HazardPort
import com.zama.safeops.modules.hazards.domain.exceptions.HazardInvalidInputException
import com.zama.safeops.modules.hazards.domain.exceptions.HazardNotFoundException
import com.zama.safeops.modules.hazards.domain.model.*
import com.zama.safeops.modules.safety.domain.model.SafetyLocationType
import org.springframework.stereotype.Service
import java.time.Instant

@Service
class HazardService(
    private val hazardPort: HazardPort,
    private val userPort: UserPort,
    private val areaPort: AreaPort,
    private val shaftPort: ShaftPort,
    private val sitePort: SitePort
) {

    fun create(req: CreateHazardRequest, creatorId: Long): Hazard {
        validateTitle(req.title)
        validateDescription(req.description)
        validateLocation(req.locationType, req.locationId)

        val priority = req.priority ?: derivePriority(req.severity)

        return hazardPort.create(
            Hazard(
                title = HazardTitle(req.title),
                description = HazardDescription(req.description),
                severity = req.severity,
                priority = priority,
                createdBy = creatorId,
                locationType = req.locationType,
                locationId = req.locationId,
                dueDate = req.dueDate
            )
        )
    }

    fun list(): List<Hazard> =
        hazardPort.findAll()

    fun get(id: Long): Hazard =
        hazardPort.findById(id) ?: throw HazardNotFoundException(id)

    fun update(
        id: Long,
        title: String,
        description: String,
        locationType: SafetyLocationType,
        locationId: Long
    ): Hazard {
        validateTitle(title)
        validateDescription(description)
        validateLocation(locationType, locationId)

        val existing = get(id)
        val updated = existing.copy(
            title = HazardTitle(title),
            description = HazardDescription(description),
            locationType = locationType,
            locationId = locationId,
            updatedAt = Instant.now()
        )
        return hazardPort.update(updated)
    }

    fun resolve(id: Long): Hazard {
        val existing = get(id)

        if (existing.status == HazardStatus.RESOLVED)
            throw HazardInvalidInputException("Hazard $id is already resolved")

        val updated = existing.copy(
            status = HazardStatus.RESOLVED,
            resolvedAt = Instant.now(),
            updatedAt = Instant.now()
        )

        return hazardPort.update(updated)
    }

    fun assign(id: Long, userId: Long): Hazard {
        if (userId <= 0 || !userPort.existsById(UserId(userId))) {
            throw HazardInvalidInputException("Invalid userId: $userId")
        }

        val existing = get(id)
        val updated = existing.copy(
            assignedTo = userId,
            status = HazardStatus.IN_PROGRESS,
            updatedAt = Instant.now()
        )
        return hazardPort.update(updated)
    }

    private fun validateTitle(title: String) {
        if (title.isBlank()) {
            throw HazardInvalidInputException("Title cannot be blank")
        }
    }

    private fun validateDescription(description: String) {
        if (description.isBlank()) {
            throw HazardInvalidInputException("Description cannot be blank")
        }
    }

    private fun validateLocation(type: SafetyLocationType, id: Long) {
        if (id <= 0) {
            throw HazardInvalidInputException("Invalid locationId: $id")
        }

        val exists = when (type) {
            SafetyLocationType.AREA -> areaPort.exists(AreaId(id))
            SafetyLocationType.SHAFT -> shaftPort.exists(ShaftId(id))
            SafetyLocationType.SITE -> sitePort.exists(SiteId(id))
        }

        if (!exists) {
            throw HazardInvalidInputException("Location $type with ID $id does not exist")
        }
    }

    private fun derivePriority(severity: HazardSeverity): HazardPriority = when (severity) {
        HazardSeverity.CRITICAL -> HazardPriority.P1
        HazardSeverity.HIGH -> HazardPriority.P2
        HazardSeverity.MEDIUM -> HazardPriority.P3
        HazardSeverity.LOW -> HazardPriority.P4
    }
}