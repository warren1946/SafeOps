/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 * Permission granted for use, modification, and distribution with attribution.
 * No warranty provided.
 */

package com.zama.safeops.modules.inspections.application.ports

import com.zama.safeops.modules.inspections.domain.model.InspectionItem
import com.zama.safeops.modules.inspections.domain.valueobjects.InspectionId
import com.zama.safeops.modules.inspections.domain.valueobjects.InspectionItemId

interface InspectionItemPort {
    fun create(item: InspectionItem): InspectionItem
    fun findByInspectionId(inspectionId: InspectionId): List<InspectionItem>
    fun findById(id: InspectionItemId): InspectionItem?
}