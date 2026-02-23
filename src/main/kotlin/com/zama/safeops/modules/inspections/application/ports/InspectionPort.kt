/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 * Permission granted for use, modification, and distribution with attribution.
 * No warranty provided.
 */

package com.zama.safeops.modules.inspections.application.ports

import com.zama.safeops.modules.inspections.domain.model.Inspection
import com.zama.safeops.modules.inspections.domain.model.InspectionFilterCriteria
import com.zama.safeops.modules.inspections.domain.valueobjects.InspectionId
import org.springframework.data.domain.Pageable
import java.time.Instant

interface InspectionPort {
    fun create(inspection: Inspection): Inspection
    fun update(inspection: Inspection): Inspection
    fun findById(id: InspectionId): Inspection?
    fun findAll(): List<Inspection>
    fun findRecent(limit: Int = 10): List<Inspection>
    fun filter(criteria: InspectionFilterCriteria): List<Inspection>
    fun findSince(since: Instant, pageable: Pageable): List<Inspection>
}