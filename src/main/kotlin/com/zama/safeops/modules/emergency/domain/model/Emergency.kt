/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 */

package com.zama.safeops.modules.emergency.domain.model

import com.zama.safeops.modules.tenant.domain.valueobjects.TenantId
import java.time.Instant

/**
 * Emergency incident aggregate root.
 */
data class Emergency(
    val id: EmergencyId,
    val tenantId: TenantId,
    val type: EmergencyType,
    val severity: EmergencySeverity,
    val status: EmergencyStatus,
    val location: EmergencyLocation,
    val reportedBy: Long?,  // User ID or null if IoT-triggered
    val description: String?,
    val affectedZones: List<String>,  // Zone IDs
    val estimatedCasualties: Int?,
    val hazardousMaterials: List<String>,
    val weatherConditions: String?,
    val responses: MutableList<EmergencyResponse> = mutableListOf(),
    val musterStatus: MutableMap<String, MusterStatus> = mutableMapOf(),
    val createdAt: Instant = Instant.now(),
    var resolvedAt: Instant? = null,
    var resolutionNotes: String? = null
) {
    fun activate(): Emergency {
        require(status == EmergencyStatus.REPORTED) { "Emergency already activated" }
        return copy(status = EmergencyStatus.ACTIVE)
    }

    fun escalate(): Emergency {
        val newSeverity = when (severity) {
            EmergencySeverity.LOW -> EmergencySeverity.MEDIUM
            EmergencySeverity.MEDIUM -> EmergencySeverity.HIGH
            EmergencySeverity.HIGH -> EmergencySeverity.CRITICAL
            EmergencySeverity.CRITICAL -> EmergencySeverity.CATASTROPHIC
            EmergencySeverity.CATASTROPHIC -> EmergencySeverity.CATASTROPHIC
        }
        return copy(severity = newSeverity)
    }

    fun addResponse(response: EmergencyResponse) {
        responses.add(response)
    }

    fun updateMusterStatus(musterPointId: String, status: MusterStatus) {
        musterStatus[musterPointId] = status
    }

    fun resolve(notes: String): Emergency {
        return copy(
            status = EmergencyStatus.RESOLVED,
            resolvedAt = Instant.now(),
            resolutionNotes = notes
        )
    }

    fun close(): Emergency {
        return copy(status = EmergencyStatus.CLOSED)
    }

    fun getAccountingRate(): Double {
        if (musterStatus.isEmpty()) return 0.0
        val accounted = musterStatus.values.sumOf { it.accountedFor }
        val expected = musterStatus.values.sumOf { it.expectedCount }
        return if (expected > 0) accounted.toDouble() / expected else 0.0
    }

    fun getMissingPersonnel(): List<Long> {
        return musterStatus.values.flatMap { it.missingPersonnelIds }
    }
}

@JvmInline
value class EmergencyId(val value: String)

enum class EmergencyType {
    FIRE,
    GAS_LEAK,
    CHEMICAL_SPILL,
    CAVE_IN,
    ROCKFALL,
    FLOOD,
    EXPLOSION,
    ELECTRICAL_HAZARD,
    MEDICAL_EMERGENCY,
    EVACUATION,
    LOCKDOWN,
    STRUCTURAL_COLLAPSE,
    MACHINERY_FAILURE,
    WEATHER_EMERGENCY,
    TERRORISM_THREAT,
    PANIC_BUTTON
}

enum class EmergencySeverity {
    LOW,           // Minor incident, local response
    MEDIUM,        // Significant incident, site response
    HIGH,          // Serious incident, external assistance
    CRITICAL,      // Major incident, full emergency response
    CATASTROPHIC   // Mass casualty, regional/national response
}

enum class EmergencyStatus {
    REPORTED,      // Initial report received
    ACTIVE,        // Emergency response in progress
    CONTAINED,     // Situation under control
    RESOLVED,      // Emergency resolved
    CLOSED         // Post-incident review complete
}

data class EmergencyLocation(
    val locationId: Long,
    val locationType: String,  // AREA, SHAFT, SITE
    val latitude: Double?,
    val longitude: Double?,
    val altitude: Double?,
    val description: String?
)

data class EmergencyResponse(
    val id: String,
    val responderId: Long,
    val responderRole: ResponderRole,
    val responseType: ResponseAction,
    val notes: String?,
    val timestamp: Instant = Instant.now(),
    val location: EmergencyLocation?
)

enum class ResponderRole {
    SAFETY_OFFICER,
    MINE_RESCUE,
    SUPERVISOR,
    MANAGEMENT,
    EXTERNAL_FIRE,
    EXTERNAL_MEDICAL,
    EXTERNAL_POLICE,
    EXTERNAL_MINE_RESCUE
}

enum class ResponseAction {
    DISPATCHED,
    ARRIVED_ON_SCENE,
    ASSESSING_SITUATION,
    CONTAINMENT_ACTION,
    EVACUATION_ASSISTANCE,
    MEDICAL_TREATMENT,
    SEARCH_AND_RESCUE,
    SITUATION_REPORT,
    STAND_DOWN
}

/**
 * Muster point for emergency assembly.
 */
data class MusterPoint(
    val id: String,
    val tenantId: TenantId,
    val name: String,
    val locationId: Long,
    val coordinate: GeoCoordinate,
    val capacity: Int,
    val facilities: List<MusterFacility>,
    val accessibility: AccessibilityInfo,
    val isActive: Boolean = true
)

data class GeoCoordinate(
    val latitude: Double,
    val longitude: Double,
    val altitude: Double? = null
)

enum class MusterFacility {
    FIRST_AID,
    FIRE_EXTINGUISHER,
    EMERGENCY_PHONE,
    SHELTER,
    LIGHTING,
    WATER_SUPPLY,
    COMMUNICATION_RADIO
}

data class AccessibilityInfo(
    val wheelchairAccessible: Boolean,
    val distanceFromWorkAreas: Int,  // meters
    val estimatedEvacuationTime: Int  // seconds
)

/**
 * Muster status during an emergency.
 */
data class MusterStatus(
    val musterPointId: String,
    val expectedCount: Int,
    val accountedFor: Int,
    val missingPersonnelIds: List<Long>,
    val lastUpdated: Instant = Instant.now()
)

/**
 * Individual muster attendance.
 */
data class MusterAttendance(
    val id: String,
    val emergencyId: EmergencyId,
    val userId: Long,
    val musterPointId: String,
    val checkedInAt: Instant = Instant.now(),
    val checkedInBy: Long?,  // Self or supervisor
    val condition: PersonnelCondition,
    val notes: String?,
    val isAccountedFor: Boolean = true
)

enum class PersonnelCondition {
    UNHARMED,
    MINOR_INJURY,
    SERIOUS_INJURY,
    CRITICAL,
    UNKNOWN
}

/**
 * Evacuation route.
 */
data class EvacuationRoute(
    val id: String,
    val tenantId: TenantId,
    val name: String,
    val fromZoneId: String,
    val toMusterPointId: String,
    val path: List<GeoCoordinate>,
    val distance: Int,  // meters
    val estimatedTime: Int,  // seconds
    val hazards: List<String>,
    val isActive: Boolean = true,
    val lastTested: Instant?
)

/**
 * Emergency contact.
 */
data class EmergencyContact(
    val id: String,
    val tenantId: TenantId,
    val name: String,
    val role: String,
    val phoneNumbers: List<String>,
    val email: String?,
    val isAvailable24x7: Boolean,
    val priority: Int,  // 1 = highest
    val notificationChannels: List<NotificationChannel>
)

enum class NotificationChannel {
    PHONE_CALL,
    SMS,
    EMAIL,
    PUSH_NOTIFICATION,
    RADIO
}

/**
 * Emergency procedure/protocol.
 */
data class EmergencyProcedure(
    val id: String,
    val tenantId: TenantId,
    val emergencyType: EmergencyType,
    val title: String,
    val steps: List<ProcedureStep>,
    val requiredEquipment: List<String>,
    val estimatedResponseTime: Int,  // minutes
    val lastReviewed: Instant,
    val reviewedBy: Long
)

data class ProcedureStep(
    val order: Int,
    val title: String,
    val description: String,
    val responsibleRole: ResponderRole,
    val estimatedDuration: Int,  // minutes
    val isCritical: Boolean
)

/**
 * Emergency drill record.
 */
data class EmergencyDrill(
    val id: String,
    val tenantId: TenantId,
    val drillType: EmergencyType,
    val scheduledDate: Instant,
    val executedDate: Instant?,
    val participants: List<Long>,
    val musterPointId: String,
    val evacuationTimeSeconds: Int?,
    val accountingRate: Double?,
    val notes: String?,
    val improvementsIdentified: List<String>,
    val status: DrillStatus
)

enum class DrillStatus {
    SCHEDULED,
    IN_PROGRESS,
    COMPLETED,
    CANCELLED
}
