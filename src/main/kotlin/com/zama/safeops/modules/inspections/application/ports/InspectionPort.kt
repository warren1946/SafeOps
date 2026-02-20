/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 * Permission granted for use, modification, and distribution with attribution.
 * No warranty provided.
 */

package com.zama.safeops.modules.inspections.application.ports

import com.zama.safeops.modules.inspections.domain.model.Inspection
import com.zama.safeops.modules.inspections.domain.valueobjects.InspectionId

interface InspectionPort {
    fun create(inspection: Inspection): Inspection
    fun update(inspection: Inspection): Inspection
    fun findById(id: InspectionId): Inspection?
    fun findAll(): List<Inspection>
}