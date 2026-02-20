/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 * Permission granted for use, modification, and distribution with attribution.
 * No warranty provided.
 */

package com.zama.safeops.modules.safety.application.ports

import com.zama.safeops.modules.safety.domain.model.SafetyReport
import com.zama.safeops.modules.safety.domain.valueobjects.SafetyReportId

interface SafetyReportPort {
    fun create(report: SafetyReport): SafetyReport
    fun findById(id: SafetyReportId): SafetyReport?
}