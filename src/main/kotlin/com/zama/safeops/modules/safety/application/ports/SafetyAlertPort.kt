/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 * Permission granted for use, modification, and distribution with attribution.
 * No warranty provided.
 */

package com.zama.safeops.modules.safety.application.ports

import com.zama.safeops.modules.safety.domain.model.SafetyAlert
import com.zama.safeops.modules.safety.domain.valueobjects.SafetyAlertId

interface SafetyAlertPort {
    fun create(alert: SafetyAlert): SafetyAlert
    fun update(alert: SafetyAlert): SafetyAlert
    fun findById(id: SafetyAlertId): SafetyAlert?
}