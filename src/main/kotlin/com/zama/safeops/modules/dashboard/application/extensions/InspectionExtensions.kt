/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 * Permission granted for use, modification, and distribution with attribution.
 * No warranty provided.
 */

package com.zama.safeops.modules.dashboard.application.extensions

import com.zama.safeops.modules.inspections.domain.model.InspectionTargetType
import com.zama.safeops.modules.safety.domain.model.SafetyLocationType

fun InspectionTargetType.toSafetyLocationType(): SafetyLocationType = when (this) {
    InspectionTargetType.AREA -> SafetyLocationType.AREA
    InspectionTargetType.SHAFT -> SafetyLocationType.SHAFT
    InspectionTargetType.SITE -> SafetyLocationType.SITE
}