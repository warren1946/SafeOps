/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 */

package com.zama.safeops.modules.emergency.application.services

import com.zama.safeops.modules.emergency.application.ports.EmergencyPort
import com.zama.safeops.modules.emergency.domain.model.*
import com.zama.safeops.modules.notification.application.services.NotificationService
import com.zama.safeops.modules.tenant.domain.valueobjects.TenantId
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.*
import java.util.concurrent.ConcurrentHashMap

/**
 * Service for managing emergency incidents and response coordination.
 */
@Service
class EmergencyResponseService(
    private val emergencyPort: EmergencyPort,
    private val notificationService: NotificationService,
    private val musterService: MusterService,
    private val evacuationService: EvacuationService
) {
    private val activeEmergencies = ConcurrentHashMap<String, Emergency>()

    /**
     * Report a new emergency.
     */
    fun reportEmergency(
        tenantId: TenantId,
        type: EmergencyType,
        location: EmergencyLocation,
        reportedBy: Long?,
        description: String?,
        severity: EmergencySeverity = EmergencySeverity.MEDIUM,
        affectedZones: List<String> = emptyList()
    ): Emergency {
        val emergency = Emergency(
            id = EmergencyId(UUID.randomUUID().toString()),
            tenantId = tenantId,
            type = type,
            severity = severity,
            status = EmergencyStatus.REPORTED,
            location = location,
            reportedBy = reportedBy,
            description = description,
            affectedZones = affectedZones,
            estimatedCasualties = null,
            hazardousMaterials = emptyList(),
            weatherConditions = null
        )

        val saved = emergencyPort.save(emergency)
        activeEmergencies[saved.id.value] = saved

        // Immediate notifications
        activateEmergencyResponse(saved)

        return saved
    }

    /**
     * Activate full emergency response.
     */
    fun activateEmergency(emergencyId: EmergencyId): Emergency {
        val emergency = emergencyPort.findById(emergencyId)
            ?: throw IllegalArgumentException("Emergency not found: $emergencyId")

        val activated = emergency.activate()
        activeEmergencies[emergencyId.value] = activated

        // Notify all emergency contacts
        notifyEmergencyContacts(activated)

        // Broadcast evacuation alert
        broadcastEvacuationAlert(activated)

        // Initialize muster points
        initializeMusterPoints(activated)

        return emergencyPort.save(activated)
    }

    /**
     * Escalate emergency severity.
     */
    fun escalateEmergency(emergencyId: EmergencyId, reason: String): Emergency {
        val emergency = emergencyPort.findById(emergencyId)
            ?: throw IllegalArgumentException("Emergency not found: $emergencyId")

        val escalated = emergency.escalate()

        // Notify of escalation
        notificationService.sendBulkNotifications(
            tenantId = emergency.tenantId,
            recipients = getEmergencyPersonnel(emergency.tenantId),
            channel = com.zama.safeops.modules.notification.domain.model.NotificationChannel.PUSH,
            type = com.zama.safeops.modules.notification.domain.model.NotificationType.SAFETY_ALERT_CRITICAL,
            subject = "🚨 EMERGENCY ESCALATED: ${emergency.type}",
            content = "Emergency escalated to ${escalated.severity}. Reason: $reason",
            priority = com.zama.safeops.modules.notification.domain.model.NotificationPriority.CRITICAL
        )

        return emergencyPort.save(escalated)
    }

    /**
     * Record a response action.
     */
    fun recordResponse(
        emergencyId: EmergencyId,
        responderId: Long,
        role: ResponderRole,
        action: ResponseAction,
        notes: String?,
        location: EmergencyLocation?
    ): Emergency {
        val emergency = emergencyPort.findById(emergencyId)
            ?: throw IllegalArgumentException("Emergency not found: $emergencyId")

        val response = EmergencyResponse(
            id = UUID.randomUUID().toString(),
            responderId = responderId,
            responderRole = role,
            responseType = action,
            notes = notes,
            location = location
        )

        emergency.addResponse(response)

        // Auto-update status based on response
        val updated = when (action) {
            ResponseAction.CONTAINMENT_ACTION ->
                emergency.copy(status = EmergencyStatus.CONTAINED)

            ResponseAction.STAND_DOWN ->
                emergency.copy(status = EmergencyStatus.RESOLVED)

            else -> emergency
        }

        return emergencyPort.save(updated)
    }

    /**
     * Record muster attendance.
     */
    fun recordMusterAttendance(
        emergencyId: EmergencyId,
        userId: Long,
        musterPointId: String,
        condition: PersonnelCondition = PersonnelCondition.UNHARMED,
        notes: String?,
        checkedInBy: Long? = null
    ): MusterAttendance {
        val attendance = MusterAttendance(
            id = UUID.randomUUID().toString(),
            emergencyId = emergencyId,
            userId = userId,
            musterPointId = musterPointId,
            checkedInBy = checkedInBy,
            condition = condition,
            notes = notes
        )

        // Update emergency muster status
        val emergency = emergencyPort.findById(emergencyId)
        emergency?.let {
            val currentStatus = it.musterStatus[musterPointId]
            val updatedStatus = currentStatus?.copy(
                accountedFor = currentStatus.accountedFor + 1,
                lastUpdated = Instant.now()
            ) ?: MusterStatus(
                musterPointId = musterPointId,
                expectedCount = 0, // Would be calculated from personnel in zone
                accountedFor = 1,
                missingPersonnelIds = emptyList()
            )

            it.updateMusterStatus(musterPointId, updatedStatus)
            emergencyPort.save(it)

            // Check if all accounted for
            if (updatedStatus.accountedFor >= updatedStatus.expectedCount) {
                notifyAllAccounted(emergencyId, musterPointId)
            }
        }

        return emergencyPort.saveMusterAttendance(attendance)
    }

    /**
     * Get muster status for an emergency.
     */
    fun getMusterStatus(emergencyId: EmergencyId): Map<String, MusterStatus> {
        val emergency = emergencyPort.findById(emergencyId)
            ?: return emptyMap()
        return emergency.musterStatus
    }

    /**
     * Get missing personnel.
     */
    fun getMissingPersonnel(emergencyId: EmergencyId): List<Long> {
        val emergency = emergencyPort.findById(emergencyId)
            ?: return emptyList()
        return emergency.getMissingPersonnel()
    }

    /**
     * Resolve an emergency.
     */
    fun resolveEmergency(emergencyId: EmergencyId, resolutionNotes: String): Emergency {
        val emergency = emergencyPort.findById(emergencyId)
            ?: throw IllegalArgumentException("Emergency not found: $emergencyId")

        val resolved = emergency.resolve(resolutionNotes)
        activeEmergencies.remove(emergencyId.value)

        // Notify all-clear
        notificationService.sendBulkNotifications(
            tenantId = emergency.tenantId,
            recipients = getAllPersonnel(emergency.tenantId),
            channel = com.zama.safeops.modules.notification.domain.model.NotificationChannel.PUSH,
            type = com.zama.safeops.modules.notification.domain.model.NotificationType.SAFETY_EVENT_REPORTED,
            subject = "✅ Emergency Resolved: ${emergency.type}",
            content = "The emergency has been resolved. $resolutionNotes",
            priority = com.zama.safeops.modules.notification.domain.model.NotificationPriority.HIGH
        )

        return emergencyPort.save(resolved)
    }

    /**
     * Get active emergencies.
     */
    fun getActiveEmergencies(tenantId: TenantId): List<Emergency> {
        return emergencyPort.findActive(tenantId)
    }

    /**
     * Get emergency by ID.
     */
    fun getEmergency(emergencyId: EmergencyId): Emergency? {
        return emergencyPort.findById(emergencyId)
    }

    /**
     * Get recommended evacuation route.
     */
    fun getEvacuationRoute(
        tenantId: TenantId,
        fromZoneId: String,
        emergencyType: EmergencyType
    ): EvacuationRoute? {
        return evacuationService.getRecommendedRoute(tenantId, fromZoneId, emergencyType)
    }

    /**
     * Schedule an emergency drill.
     */
    fun scheduleDrill(
        tenantId: TenantId,
        drillType: EmergencyType,
        scheduledDate: Instant,
        musterPointId: String,
        participants: List<Long>
    ): EmergencyDrill {
        val drill = EmergencyDrill(
            id = UUID.randomUUID().toString(),
            tenantId = tenantId,
            drillType = drillType,
            scheduledDate = scheduledDate,
            executedDate = null,
            participants = participants,
            musterPointId = musterPointId,
            evacuationTimeSeconds = null,
            accountingRate = null,
            notes = null,
            improvementsIdentified = emptyList(),
            status = DrillStatus.SCHEDULED
        )

        return emergencyPort.saveDrill(drill)
    }

    /**
     * Get upcoming drills.
     */
    fun getUpcomingDrills(tenantId: TenantId): List<EmergencyDrill> {
        return emergencyPort.findUpcomingDrills(tenantId)
    }

    /**
     * Check for missed drills and alert.
     */
    @Scheduled(cron = "0 0 8 * * MON") // Every Monday 8 AM
    fun checkDrillSchedule() {
        // Implementation would check for overdue drills
    }

    private fun activateEmergencyResponse(emergency: Emergency) {
        // Immediate actions when emergency is reported
        when (emergency.type) {
            EmergencyType.FIRE -> activateFireResponse(emergency)
            EmergencyType.GAS_LEAK -> activateGasLeakResponse(emergency)
            EmergencyType.CAVE_IN -> activateCaveInResponse(emergency)
            EmergencyType.MEDICAL_EMERGENCY -> activateMedicalResponse(emergency)
            EmergencyType.EVACUATION -> activateEvacuation(emergency)
            else -> activateGeneralResponse(emergency)
        }
    }

    private fun activateFireResponse(emergency: Emergency) {
        // Fire-specific response
        notificationService.sendBulkNotifications(
            tenantId = emergency.tenantId,
            recipients = getFireResponseTeam(emergency.tenantId),
            channel = com.zama.safeops.modules.notification.domain.model.NotificationChannel.PUSH,
            type = com.zama.safeops.modules.notification.domain.model.NotificationType.SAFETY_ALERT_CRITICAL,
            subject = "🔥 FIRE EMERGENCY",
            content = "Fire reported at ${emergency.location.description}. All personnel evacuate immediately!",
            priority = com.zama.safeops.modules.notification.domain.model.NotificationPriority.CRITICAL
        )
    }

    private fun activateGasLeakResponse(emergency: Emergency) {
        notificationService.sendBulkNotifications(
            tenantId = emergency.tenantId,
            recipients = getAllPersonnel(emergency.tenantId),
            channel = com.zama.safeops.modules.notification.domain.model.NotificationChannel.PUSH,
            type = com.zama.safeops.modules.notification.domain.model.NotificationType.SAFETY_ALERT_CRITICAL,
            subject = "☠️ GAS LEAK DETECTED",
            content = "Gas leak at ${emergency.location.description}. DO NOT USE ELECTRICAL EQUIPMENT. Evacuate immediately!",
            priority = com.zama.safeops.modules.notification.domain.model.NotificationPriority.CRITICAL
        )
    }

    private fun activateCaveInResponse(emergency: Emergency) {
        // Mine rescue team activation
        notificationService.sendBulkNotifications(
            tenantId = emergency.tenantId,
            recipients = getMineRescueTeam(emergency.tenantId),
            channel = com.zama.safeops.modules.notification.domain.model.NotificationChannel.PUSH,
            type = com.zama.safeops.modules.notification.domain.model.NotificationType.SAFETY_ALERT_CRITICAL,
            subject = "⛏️ CAVE-IN / ROCKFALL",
            content = "Structural collapse at ${emergency.location.description}. Mine rescue team respond immediately!",
            priority = com.zama.safeops.modules.notification.domain.model.NotificationPriority.CRITICAL
        )
    }

    private fun activateMedicalResponse(emergency: Emergency) {
        notificationService.sendBulkNotifications(
            tenantId = emergency.tenantId,
            recipients = getMedicalResponseTeam(emergency.tenantId),
            channel = com.zama.safeops.modules.notification.domain.model.NotificationChannel.PUSH,
            type = com.zama.safeops.modules.notification.domain.model.NotificationType.SAFETY_ALERT_CRITICAL,
            subject = "🏥 MEDICAL EMERGENCY",
            content = "Medical emergency at ${emergency.location.description}. First responders to scene immediately!",
            priority = com.zama.safeops.modules.notification.domain.model.NotificationPriority.CRITICAL
        )
    }

    private fun activateEvacuation(emergency: Emergency) {
        broadcastEvacuationAlert(emergency)
    }

    private fun activateGeneralResponse(emergency: Emergency) {
        notifyEmergencyContacts(emergency)
    }

    private fun notifyEmergencyContacts(emergency: Emergency) {
        val contacts = emergencyPort.getEmergencyContacts(emergency.tenantId)

        contacts.filter { it.priority <= 3 }.forEach { contact ->
            contact.notificationChannels.forEach { channel ->
                when (channel) {
                    NotificationChannel.PHONE_CALL -> initiatePhoneCall(contact, emergency)
                    NotificationChannel.SMS -> sendSMS(contact, emergency)
                    NotificationChannel.PUSH_NOTIFICATION -> {
                        // Already handled by bulk notification
                    }

                    else -> { /* Other channels */
                    }
                }
            }
        }
    }

    private fun broadcastEvacuationAlert(emergency: Emergency) {
        // Broadcast to all personnel in affected zones
        notificationService.sendBulkNotifications(
            tenantId = emergency.tenantId,
            recipients = getPersonnelInZones(emergency.tenantId, emergency.affectedZones),
            channel = com.zama.safeops.modules.notification.domain.model.NotificationChannel.PUSH,
            type = com.zama.safeops.modules.notification.domain.model.NotificationType.SAFETY_ALERT_CRITICAL,
            subject = "🚨 EMERGENCY EVACUATION",
            content = "EMERGENCY: ${emergency.type} reported. Evacuate to nearest muster point immediately!",
            priority = com.zama.safeops.modules.notification.domain.model.NotificationPriority.CRITICAL
        )
    }

    private fun initializeMusterPoints(emergency: Emergency) {
        val musterPoints = musterService.getMusterPointsForZones(
            emergency.tenantId,
            emergency.affectedZones
        )

        musterPoints.forEach { musterPoint ->
            val expectedCount = musterService.getExpectedPersonnelCount(
                emergency.tenantId,
                musterPoint.id
            )

            val status = MusterStatus(
                musterPointId = musterPoint.id,
                expectedCount = expectedCount,
                accountedFor = 0,
                missingPersonnelIds = emptyList()
            )

            emergency.updateMusterStatus(musterPoint.id, status)
        }

        emergencyPort.save(emergency)
    }

    private fun notifyAllAccounted(emergencyId: EmergencyId, musterPointId: String) {
        notificationService.sendBulkNotifications(
            tenantId = TenantId(1), // Would get from emergency
            recipients = getEmergencyPersonnel(TenantId(1)),
            channel = com.zama.safeops.modules.notification.domain.model.NotificationChannel.PUSH,
            type = com.zama.safeops.modules.notification.domain.model.NotificationType.SAFETY_EVENT_REPORTED,
            subject = "✅ All Personnel Accounted",
            content = "All personnel at muster point $musterPointId have been accounted for.",
            priority = com.zama.safeops.modules.notification.domain.model.NotificationPriority.HIGH
        )
    }

    // Placeholder methods for personnel lookup
    private fun getEmergencyPersonnel(tenantId: TenantId):
            List<com.zama.safeops.modules.notification.domain.model.Recipient> = emptyList()

    private fun getFireResponseTeam(tenantId: TenantId):
            List<com.zama.safeops.modules.notification.domain.model.Recipient> = emptyList()

    private fun getMineRescueTeam(tenantId: TenantId):
            List<com.zama.safeops.modules.notification.domain.model.Recipient> = emptyList()

    private fun getMedicalResponseTeam(tenantId: TenantId):
            List<com.zama.safeops.modules.notification.domain.model.Recipient> = emptyList()

    private fun getAllPersonnel(tenantId: TenantId):
            List<com.zama.safeops.modules.notification.domain.model.Recipient> = emptyList()

    private fun getPersonnelInZones(tenantId: TenantId, zones: List<String>):
            List<com.zama.safeops.modules.notification.domain.model.Recipient> = emptyList()

    private fun initiatePhoneCall(contact: EmergencyContact, emergency: Emergency) {}
    private fun sendSMS(contact: EmergencyContact, emergency: Emergency) {}
}

// Supporting services
@Service
class MusterService {
    fun getMusterPointsForZones(tenantId: TenantId, zones: List<String>): List<MusterPoint> = emptyList()
    fun getExpectedPersonnelCount(tenantId: TenantId, musterPointId: String): Int = 0
}

@Service
class EvacuationService {
    fun getRecommendedRoute(tenantId: TenantId, fromZoneId: String, emergencyType: EmergencyType):
            EvacuationRoute? = null
}
