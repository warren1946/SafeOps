/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 * Permission granted for use, modification, and distribution with attribution.
 * No warranty provided.
 */

package com.zama.safeops.modules.hazards.application.services

import com.zama.safeops.modules.hazards.application.ports.HazardPort
import com.zama.safeops.modules.hazards.domain.exceptions.HazardInvalidInputException
import com.zama.safeops.modules.hazards.domain.exceptions.HazardNotFoundException
import com.zama.safeops.modules.hazards.domain.model.Hazard
import com.zama.safeops.modules.hazards.domain.model.HazardDescription
import com.zama.safeops.modules.hazards.domain.model.HazardStatus
import com.zama.safeops.modules.hazards.domain.model.HazardTitle
import org.springframework.stereotype.Service
import java.time.Instant

@Service
class HazardService(
    private val hazardPort: HazardPort
) {

    fun create(title: String, description: String): Hazard {
        validateTitle(title)
        validateDescription(description)

        return hazardPort.create(
            Hazard(
                title = HazardTitle(title),
                description = HazardDescription(description)
            )
        )
    }

    fun list(): List<Hazard> =
        hazardPort.findAll()

    fun get(id: Long): Hazard =
        hazardPort.findById(id) ?: throw HazardNotFoundException(id)

    fun update(id: Long, title: String, description: String): Hazard {
        validateTitle(title)
        validateDescription(description)

        val existing = get(id)
        val updated = existing.copy(
            title = HazardTitle(title),
            description = HazardDescription(description),
            updatedAt = Instant.now()
        )
        return hazardPort.update(updated)
    }

    fun resolve(id: Long): Hazard {
        val existing = get(id)

        if (existing.status == HazardStatus.RESOLVED) {
            throw HazardInvalidInputException("Hazard $id is already resolved")
        }

        val updated = existing.copy(
            status = HazardStatus.RESOLVED,
            updatedAt = Instant.now()
        )
        return hazardPort.update(updated)
    }

    fun assign(id: Long, userId: Long): Hazard {
        if (userId <= 0) {
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
}