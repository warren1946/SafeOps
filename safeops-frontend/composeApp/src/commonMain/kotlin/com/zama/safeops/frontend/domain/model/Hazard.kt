package com.zama.safeops.frontend.domain.model

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class Hazard(
    val id: Long,
    val title: String,
    val description: String? = null,
    val category: HazardCategory,
    val severity: HazardSeverity,
    val status: HazardStatus,
    val location: String? = null,
    val reportedBy: String? = null,
    val assignedTo: String? = null,
    val createdAt: Instant,
    val resolvedAt: Instant? = null,
    val imageUrl: String? = null
)

@Serializable
enum class HazardCategory {
    PHYSICAL,
    CHEMICAL,
    BIOLOGICAL,
    ERGONOMIC,
    PSYCHOLOGICAL,
    ELECTRICAL,
    MECHANICAL,
    ENVIRONMENTAL,
    FIRE_SAFETY,
    OTHER
}

@Serializable
enum class HazardSeverity {
    LOW,
    MEDIUM,
    HIGH,
    CRITICAL
}

@Serializable
enum class HazardStatus {
    OPEN,
    IN_PROGRESS,
    RESOLVED,
    CLOSED
}
