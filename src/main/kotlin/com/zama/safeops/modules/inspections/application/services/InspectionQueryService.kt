/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 * Permission granted for use, modification, and distribution with attribution.
 * No warranty provided.
 */

package com.zama.safeops.modules.inspections.application.services

import com.zama.safeops.modules.dashboard.application.extensions.toSafetyLocationType
import com.zama.safeops.modules.inspections.api.mappers.calculateScore
import com.zama.safeops.modules.inspections.application.ports.InspectionPort
import com.zama.safeops.modules.inspections.domain.model.Inspection
import com.zama.safeops.modules.inspections.domain.model.InspectionFilterCriteria
import com.zama.safeops.modules.inspections.domain.model.InspectionSortField
import com.zama.safeops.modules.inspections.domain.model.SortDirection
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.ZoneOffset

@Service
class InspectionQueryService(private val inspectionPort: InspectionPort) {

    @Transactional(readOnly = true)
    fun getFiltered(criteria: InspectionFilterCriteria): List<Inspection> {
        val all = inspectionPort.findAll()

        val filtered = all
            .filter { matchesStatus(criteria, it) }
            .filter { matchesDate(criteria, it) }
            .filter { matchesOfficer(criteria, it) }
            .filter { matchesLocation(criteria, it) }
            .filter { matchesSearch(criteria, it) }

        return sort(criteria, filtered)
    }

    private fun matchesStatus(c: InspectionFilterCriteria, i: Inspection): Boolean =
        c.status?.let { it == i.status } ?: true

    private fun matchesDate(c: InspectionFilterCriteria, i: Inspection): Boolean {
        val date = i.createdAt.atZone(ZoneOffset.UTC).toLocalDate()

        if (c.fromDate != null && date.isBefore(c.fromDate)) return false
        if (c.toDate != null && date.isAfter(c.toDate)) return false

        return true
    }

    private fun matchesOfficer(c: InspectionFilterCriteria, i: Inspection): Boolean = c.officerId?.let { it == i.inspectorId } ?: true

    private fun matchesLocation(c: InspectionFilterCriteria, i: Inspection): Boolean {
        if (c.locationType == null && c.locationId == null) return true

        val inspectionLocationType = i.targetType.toSafetyLocationType()
        val inspectionLocationId = i.targetId

        if (c.locationType != null && c.locationType != inspectionLocationType) return false
        if (c.locationId != null && c.locationId != inspectionLocationId) return false

        return true
    }

    private fun matchesSearch(c: InspectionFilterCriteria, i: Inspection): Boolean {
        val q = c.search?.trim()?.lowercase() ?: return true
        if (q.isBlank()) return true

        val title = i.title.lowercase()

        return title.contains(q)
    }

    private fun sort(c: InspectionFilterCriteria, list: List<Inspection>): List<Inspection> {
        val comparator = when (c.sortBy) {
            InspectionSortField.DATE ->
                compareBy<Inspection> { it.createdAt }

            InspectionSortField.SCORE ->
                compareBy<Inspection> { it.calculateScore().percentage }

            InspectionSortField.STATUS ->
                compareBy<Inspection> { it.status.name }

            InspectionSortField.OFFICER ->
                compareBy<Inspection> { it.inspectorId }
        }

        val sorted = list.sortedWith(comparator)

        return if (c.direction == SortDirection.ASC) sorted else sorted.reversed()
    }
}