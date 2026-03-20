package com.zama.safeops.frontend.domain.model

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class Inspection(
    val id: Long,
    val title: String,
    val type: InspectionType,
    val status: InspectionStatus,
    val location: String? = null,
    val scheduledDate: Instant? = null,
    val completedDate: Instant? = null,
    val inspectorName: String? = null,
    val score: Int? = null,
    val findingsCount: Int = 0,
    val createdAt: Instant
)

@Serializable
enum class InspectionType {
    GENERAL,
    EQUIPMENT,
    WORKPLACE,
    ENVIRONMENTAL,
    EMERGENCY_EQUIPMENT,
    PPE_COMPLIANCE
}

@Serializable
enum class InspectionStatus {
    SCHEDULED,
    IN_PROGRESS,
    COMPLETED,
    OVERDUE,
    CANCELLED
}
