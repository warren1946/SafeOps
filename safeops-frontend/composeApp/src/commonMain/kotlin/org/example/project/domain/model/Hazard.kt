package org.example.project.domain.model

/**
 * Domain model for Hazard
 */
data class Hazard(
    val id: Long,
    val title: String,
    val description: String? = null,
    val severity: HazardSeverity = HazardSeverity.MEDIUM,
    val status: HazardStatus = HazardStatus.OPEN,
    val location: String? = null,
    val reportedBy: Long? = null,
    val assignedTo: Long? = null,
    val createdAt: String? = null,
    val resolvedAt: String? = null
)

enum class HazardSeverity {
    CRITICAL, HIGH, MEDIUM, LOW;
    
    companion object {
        fun fromString(severity: String): HazardSeverity = when (severity.uppercase()) {
            "CRITICAL" -> CRITICAL
            "HIGH" -> HIGH
            "MEDIUM" -> MEDIUM
            "LOW" -> LOW
            else -> MEDIUM
        }
    }
}

enum class HazardStatus {
    OPEN, IN_PROGRESS, RESOLVED, CLOSED;
    
    companion object {
        fun fromString(status: String): HazardStatus = when (status.uppercase()) {
            "OPEN" -> OPEN
            "IN_PROGRESS" -> IN_PROGRESS
            "RESOLVED" -> RESOLVED
            "CLOSED" -> CLOSED
            else -> OPEN
        }
    }
}
