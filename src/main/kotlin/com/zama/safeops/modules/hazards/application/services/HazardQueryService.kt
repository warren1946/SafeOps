/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 * Permission granted for use, modification, and distribution with attribution.
 * No warranty provided.
 */

package com.zama.safeops.modules.hazards.application.services

import com.zama.safeops.modules.hazards.api.dto.HazardFilterCriteria
import com.zama.safeops.modules.hazards.api.dto.HazardSortField
import com.zama.safeops.modules.hazards.application.ports.HazardPort
import com.zama.safeops.modules.hazards.domain.model.Hazard
import com.zama.safeops.modules.shared.entities.SortDirection
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class HazardQueryService(
    private val hazardPort: HazardPort
) {

    @Transactional(readOnly = true)
    fun getFiltered(criteria: HazardFilterCriteria): List<Hazard> {
        val all = hazardPort.findAll()

        val filtered = all
            .filter { matchesStatus(criteria, it) }
            .filter { matchesAssigned(criteria, it) }
            .filter { matchesLocation(criteria, it) }
            .filter { matchesSearch(criteria, it) }

        return sort(criteria, filtered)
    }

    private fun matchesStatus(c: HazardFilterCriteria, h: Hazard): Boolean =
        c.status?.let { it == h.status } ?: true

    private fun matchesAssigned(c: HazardFilterCriteria, h: Hazard): Boolean =
        c.assignedTo?.let { it == h.assignedTo } ?: true

    private fun matchesLocation(c: HazardFilterCriteria, h: Hazard): Boolean {
        if (c.locationType == null && c.locationId == null) return true

        if (c.locationType != null && c.locationType != h.locationType) return false
        if (c.locationId != null && c.locationId != h.locationId) return false

        return true
    }

    private fun matchesSearch(c: HazardFilterCriteria, h: Hazard): Boolean {
        val q = c.search?.trim()?.lowercase() ?: return true
        if (q.isBlank()) return true

        return h.title.value.lowercase().contains(q) ||
                h.description.value.lowercase().contains(q)
    }

    private fun sort(c: HazardFilterCriteria, list: List<Hazard>): List<Hazard> {
        val comparator = when (c.sortBy) {
            HazardSortField.DATE -> compareBy<Hazard> { it.createdAt }
            HazardSortField.STATUS -> compareBy<Hazard> { it.status.name }
            HazardSortField.ASSIGNED_TO -> compareBy<Hazard> { it.assignedTo ?: Long.MAX_VALUE }
            HazardSortField.LOCATION -> compareBy<Hazard> { it.locationId }
            HazardSortField.TITLE -> compareBy<Hazard> { it.title.value.lowercase() }
            HazardSortField.SEVERITY -> compareBy<Hazard> { it.severity }
            HazardSortField.PRIORITY -> compareBy<Hazard> { it.priority }
            HazardSortField.DUE_DATE -> compareBy<Hazard> { it.dueDate }
        }

        val sorted = list.sortedWith(comparator)
        return if (c.direction == SortDirection.ASC) sorted else sorted.reversed()
    }
}