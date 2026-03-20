/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 * Permission granted for use, modification, and distribution with attribution.
 * No warranty provided.
 */

package com.zama.safeops.modules.hazards.domain.model

import com.zama.safeops.modules.safety.domain.model.SafetyLocationType
import java.time.Instant

data class Hazard(
    val id: HazardId? = null,
    val title: HazardTitle,
    val description: HazardDescription,
    val severity: HazardSeverity = HazardSeverity.MEDIUM,
    val priority: HazardPriority = HazardPriority.P3,
    val status: HazardStatus = HazardStatus.OPEN,
    val assignedTo: Long? = null,
    val createdBy: Long? = null,
    val locationType: SafetyLocationType? = null,
    val locationId: Long? = null,
    val dueDate: Instant? = null,
    val resolvedAt: Instant? = null,
    val createdAt: Instant = Instant.now(),
    val updatedAt: Instant = Instant.now()
)


@JvmInline
value class HazardId(val value: Long)

@JvmInline
value class HazardTitle(val value: String)

@JvmInline
value class HazardDescription(val value: String)

enum class HazardStatus {
    OPEN,
    IN_PROGRESS,
    RESOLVED
}

enum class HazardSeverity {
    LOW,
    MEDIUM,
    HIGH,
    CRITICAL
}

enum class HazardPriority {
    P1,
    P2,
    P3,
    P4
}
