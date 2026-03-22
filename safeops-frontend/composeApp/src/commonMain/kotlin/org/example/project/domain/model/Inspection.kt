package org.example.project.domain.model

/**
 * Domain model for Inspection
 */
data class Inspection(
    val id: Long,
    val title: String,
    val targetType: InspectionTargetType,
    val targetId: Long,
    val templateId: Long? = null,
    val assignedOfficerId: Long? = null,
    val status: InspectionStatus = InspectionStatus.PENDING,
    val createdAt: String? = null,
    val completedAt: String? = null
)

enum class InspectionTargetType {
    AREA, SHAFT, SITE;
    
    companion object {
        fun fromString(type: String): InspectionTargetType = when (type.uppercase()) {
            "AREA" -> AREA
            "SHAFT" -> SHAFT
            "SITE" -> SITE
            else -> AREA
        }
    }
}

enum class InspectionStatus {
    PENDING, IN_PROGRESS, COMPLETED, CANCELLED;
    
    companion object {
        fun fromString(status: String): InspectionStatus = when (status.uppercase()) {
            "PENDING" -> PENDING
            "IN_PROGRESS" -> IN_PROGRESS
            "COMPLETED" -> COMPLETED
            "CANCELLED" -> CANCELLED
            else -> PENDING
        }
    }
}
