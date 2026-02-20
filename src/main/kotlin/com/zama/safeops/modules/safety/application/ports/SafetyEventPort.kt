/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 * Permission granted for use, modification, and distribution with attribution.
 * No warranty provided.
 */

package com.zama.safeops.modules.safety.application.ports

import com.zama.safeops.modules.safety.domain.model.SafetyEvent
import com.zama.safeops.modules.safety.domain.valueobjects.SafetyEventId
import java.time.Instant

interface SafetyEventPort {
    fun create(event: SafetyEvent): SafetyEvent
    fun findById(id: SafetyEventId): SafetyEvent?
    fun findByPeriod(start: Instant, end: Instant): List<SafetyEvent>
}
