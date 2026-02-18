/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 * Permission granted for use, modification, and distribution with attribution.
 * No warranty provided.
 */

package com.zama.safeops.modules.hazards.domain.model

import java.time.Instant

data class Hazard(
    val id: HazardId? = null,
    val title: HazardTitle,
    val description: HazardDescription,
    val status: HazardStatus = HazardStatus.OPEN,
    val assignedTo: Long? = null,
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