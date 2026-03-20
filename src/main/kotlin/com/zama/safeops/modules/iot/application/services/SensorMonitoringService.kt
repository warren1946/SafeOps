/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 */

package com.zama.safeops.modules.iot.application.services

import com.zama.safeops.modules.hazards.application.ports.HazardPort
import com.zama.safeops.modules.hazards.domain.model.*
import com.zama.safeops.modules.iot.application.ports.GeoFencingService
import com.zama.safeops.modules.iot.application.ports.SensorDataPort
import com.zama.safeops.modules.iot.domain.model.*
import com.zama.safeops.modules.notification.application.services.NotificationService
import com.zama.safeops.modules.safety.domain.model.SafetyLocationType
import com.zama.safeops.modules.safety.domain.model.SafetySeverity
import com.zama.safeops.modules.tenant.domain.valueobjects.TenantId
import org.springframework.stereotype.Service
import java.time.Instant

/**
 * Service for real-time sensor monitoring and alerting.
 */
@Service
class SensorMonitoringService(
    private val sensorDataPort: SensorDataPort,
    private val hazardPort: HazardPort,
    private val notificationService: NotificationService,
    private val geoFencingService: GeoFencingService
) {

    /**
     * Process incoming sensor reading.
     * Checks thresholds and creates alerts if needed.
     */
    fun processSensorReading(reading: SensorReading, tenantId: TenantId) {
        // Persist reading
        sensorDataPort.saveReading(reading)

        // Get threshold for this sensor type/location
        val threshold = sensorDataPort.getThreshold(reading.sensorType, reading.locationId)

        // Check for threshold violations
        threshold?.checkValue(reading.value)?.let { violation ->
            handleThresholdViolation(reading, violation, tenantId)
        }

        // Check for device health
        checkDeviceHealth(reading)
    }

    /**
     * Process panic button activation.
     */
    fun processPanicButton(userId: Long, deviceId: String, location: GeoCoordinate, tenantId: TenantId) {
        // Create critical safety event
        val hazard = Hazard(
            id = null,
            title = HazardTitle("🚨 PANIC BUTTON ACTIVATED"),
            description = HazardDescription(
                """
                Officer $userId activated panic button.
                Device: $deviceId
                Location: ${location.latitude}, ${location.longitude}
                Time: ${Instant.now()}
            """.trimIndent()
            ),
            severity = HazardSeverity.CRITICAL,
            status = HazardStatus.OPEN,
            createdBy = userId,
            locationType = SafetyLocationType.AREA, // Default, should be determined from device
            locationId = 0 // Should be determined from device
        )

        val saved = hazardPort.create(hazard)

        // Immediate notifications
        notificationService.sendBulkNotifications(
            tenantId = tenantId,
            recipients = getEmergencyContacts(tenantId),
            channel = com.zama.safeops.modules.notification.domain.model.NotificationChannel.PUSH,
            type = com.zama.safeops.modules.notification.domain.model.NotificationType.SAFETY_ALERT_CRITICAL,
            subject = "🚨 EMERGENCY: Panic Button Activated",
            content = "Officer $userId activated panic button at ${location.latitude}, ${location.longitude}",
            priority = com.zama.safeops.modules.notification.domain.model.NotificationPriority.CRITICAL
        )
    }

    /**
     * Process personnel location update.
     */
    fun updatePersonnelLocation(location: PersonnelLocation, tenantId: TenantId) {
        sensorDataPort.savePersonnelLocation(location)

        // Check geofence violations
        val zoneViolation = geoFencingService.checkZoneAccess(
            location.userId,
            location.coordinate
        )

        if (zoneViolation != null) {
            handleZoneViolation(location, zoneViolation, tenantId)
        }

        // Check if in muster point during emergency
        if (isEmergencyActive(tenantId)) {
            checkMusterStatus(location, tenantId)
        }
    }

    /**
     * Get real-time dashboard data.
     */
    fun getRealTimeStatus(tenantId: TenantId): IoTDashboardStatus {
        val activeAlerts = sensorDataPort.getActiveAlerts(tenantId)
        val deviceStatuses = sensorDataPort.getDeviceStatuses(tenantId)
        val personnelInField = sensorDataPort.getActivePersonnelCount(tenantId)

        return IoTDashboardStatus(
            activeAlerts = activeAlerts.size,
            criticalAlerts = activeAlerts.count { it.severity == SafetySeverity.CRITICAL },
            onlineDevices = deviceStatuses.count { it.status == DeviceStatus.ONLINE },
            offlineDevices = deviceStatuses.count { it.status == DeviceStatus.OFFLINE },
            lowBatteryDevices = deviceStatuses.count { it.status == DeviceStatus.LOW_BATTERY },
            personnelInField = personnelInField,
            recentReadings = sensorDataPort.getRecentReadings(tenantId, limit = 10),
            activeAlertsDetails = activeAlerts
        )
    }

    /**
     * Get sensor history for analytics.
     */
    fun getSensorHistory(
        sensorId: String,
        from: Instant,
        to: Instant
    ): List<SensorReading> {
        return sensorDataPort.getReadingsInRange(sensorId, from, to)
    }

    private fun handleThresholdViolation(
        reading: SensorReading,
        violation: ThresholdViolation,
        tenantId: TenantId
    ) {
        // Create hazard for critical violations
        if (violation.severity == SafetySeverity.CRITICAL) {
            val hazard = createHazardFromReading(reading, violation, tenantId)
            hazardPort.create(hazard)
        }

        // Send immediate alert
        val notificationType = when (violation.severity) {
            SafetySeverity.CRITICAL ->
                com.zama.safeops.modules.notification.domain.model.NotificationType.SAFETY_ALERT_CRITICAL

            SafetySeverity.HIGH ->
                com.zama.safeops.modules.notification.domain.model.NotificationType.SAFETY_ALERT_HIGH

            else ->
                com.zama.safeops.modules.notification.domain.model.NotificationType.SAFETY_EVENT_REPORTED
        }

        notificationService.sendBulkNotifications(
            tenantId = tenantId,
            recipients = getLocationSupervisors(tenantId, reading.locationId),
            channel = com.zama.safeops.modules.notification.domain.model.NotificationChannel.PUSH,
            type = notificationType,
            subject = "${violation.severity} Alert: ${reading.sensorType}",
            content = violation.message,
            priority = mapSeverityToPriority(violation.severity)
        )
    }

    private fun handleZoneViolation(
        location: PersonnelLocation,
        violation: ZoneViolation,
        tenantId: TenantId
    ) {
        // Log violation
        sensorDataPort.saveZoneViolation(location, violation)

        // Alert if unauthorized zone entry
        if (violation.zoneType == ZoneType.RESTRICTED) {
            notificationService.sendTemplatedNotification(
                tenantId = tenantId,
                recipient = com.zama.safeops.modules.notification.domain.model.Recipient(userId = location.userId),
                channel = com.zama.safeops.modules.notification.domain.model.NotificationChannel.PUSH,
                templateId = "unauthorized_zone_entry",
                templateData = mapOf(
                    "userId" to location.userId,
                    "zoneName" to violation.zoneName
                ),
                priority = com.zama.safeops.modules.notification.domain.model.NotificationPriority.HIGH
            )
        }
    }

    private fun createHazardFromReading(
        reading: SensorReading,
        violation: ThresholdViolation,
        tenantId: TenantId
    ): Hazard {
        return Hazard(
            id = null,
            title = HazardTitle("IoT Alert: ${reading.sensorType} Threshold Exceeded"),
            description = HazardDescription(
                """
                Sensor ${reading.sensorId} detected critical value.
                ${violation.message}
                Location: ${reading.locationId}
                Time: ${reading.timestamp}
            """.trimIndent()
            ),
            severity = mapToHazardSeverity(violation.severity),
            status = HazardStatus.OPEN,
            locationType = reading.locationType.let {
                when (it) {
                    LocationType.AREA -> SafetyLocationType.AREA
                    LocationType.SHAFT -> SafetyLocationType.SHAFT
                    LocationType.SITE -> SafetyLocationType.SITE
                    else -> SafetyLocationType.AREA
                }
            },
            locationId = reading.locationId
        )
    }

    private fun checkDeviceHealth(reading: SensorReading) {
        val lastReading = sensorDataPort.getLastReading(reading.sensorId)

        // Check if device has stopped reporting
        if (lastReading != null) {
            val minutesSinceLastReading = java.time.Duration.between(lastReading.timestamp, Instant.now()).toMinutes()
            if (minutesSinceLastReading > 10) {
                sensorDataPort.updateDeviceStatus(reading.sensorId, DeviceStatus.OFFLINE)
            }
        }
    }

    private fun checkMusterStatus(location: PersonnelLocation, tenantId: TenantId) {
        // Logic to check if personnel are at muster points during emergency
    }

    private fun mapSeverityToPriority(severity: SafetySeverity):
            com.zama.safeops.modules.notification.domain.model.NotificationPriority =
        when (severity) {
            SafetySeverity.CRITICAL ->
                com.zama.safeops.modules.notification.domain.model.NotificationPriority.CRITICAL

            SafetySeverity.HIGH ->
                com.zama.safeops.modules.notification.domain.model.NotificationPriority.HIGH

            SafetySeverity.MEDIUM ->
                com.zama.safeops.modules.notification.domain.model.NotificationPriority.NORMAL

            SafetySeverity.LOW ->
                com.zama.safeops.modules.notification.domain.model.NotificationPriority.LOW
        }

    private fun mapToHazardSeverity(severity: SafetySeverity): HazardSeverity = when (severity) {
        SafetySeverity.CRITICAL -> HazardSeverity.CRITICAL
        SafetySeverity.HIGH -> HazardSeverity.HIGH
        SafetySeverity.MEDIUM -> HazardSeverity.MEDIUM
        SafetySeverity.LOW -> HazardSeverity.LOW
    }

    private fun getEmergencyContacts(tenantId: TenantId):
            List<com.zama.safeops.modules.notification.domain.model.Recipient> {
        // TODO: Get emergency contacts from tenant config
        return emptyList()
    }

    private fun getLocationSupervisors(tenantId: TenantId, locationId: Long):
            List<com.zama.safeops.modules.notification.domain.model.Recipient> {
        // TODO: Get supervisors for location
        return emptyList()
    }

    private fun isEmergencyActive(tenantId: TenantId): Boolean {
        // TODO: Check if there's an active emergency
        return false
    }
}

// Supporting classes
data class IoTDashboardStatus(
    val activeAlerts: Int,
    val criticalAlerts: Int,
    val onlineDevices: Int,
    val offlineDevices: Int,
    val lowBatteryDevices: Int,
    val personnelInField: Int,
    val recentReadings: List<SensorReading>,
    val activeAlertsDetails: List<ActiveAlert>
)
