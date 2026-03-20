/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 */

package com.zama.safeops.modules.iot.domain.model

import com.zama.safeops.modules.safety.domain.model.SafetySeverity
import java.time.Instant

/**
 * Represents a sensor reading from IoT devices.
 */
data class SensorReading(
    val id: String,
    val sensorId: String,
    val sensorType: SensorType,
    val locationId: Long,
    val locationType: LocationType,
    val value: Double,
    val unit: String,
    val timestamp: Instant = Instant.now(),
    val metadata: Map<String, String> = emptyMap()
)

enum class SensorType {
    // Gas Detection
    METHANE,
    CARBON_MONOXIDE,
    HYDROGEN_SULFIDE,
    OXYGEN_LEVEL,

    // Environmental
    DUST_PARTICULATE,    // PM2.5, PM10
    NOISE_LEVEL,         // Decibels
    TEMPERATURE,
    HUMIDITY,
    AIR_PRESSURE,
    VIBRATION,

    // Safety
    PROXIMITY,           // Personnel proximity
    MOTION_DETECTION,
    FALL_DETECTION,
    PANIC_BUTTON,

    // Equipment
    EQUIPMENT_TEMPERATURE,
    HYDRAULIC_PRESSURE,
    ELECTRICAL_CURRENT,
    BATTERY_LEVEL
}

enum class LocationType {
    AREA,
    SHAFT,
    SITE,
    EQUIPMENT
}

/**
 * Alert threshold configuration for sensors.
 */
data class SensorThreshold(
    val sensorType: SensorType,
    val locationId: Long,
    val warningMin: Double?,
    val warningMax: Double?,
    val criticalMin: Double?,
    val criticalMax: Double?,
    val unit: String
) {
    fun checkValue(value: Double): ThresholdViolation? {
        return when {
            criticalMin != null && value < criticalMin ->
                ThresholdViolation(SafetySeverity.CRITICAL, "Below critical minimum: $value $unit (min: $criticalMin)")

            criticalMax != null && value > criticalMax ->
                ThresholdViolation(SafetySeverity.CRITICAL, "Above critical maximum: $value $unit (max: $criticalMax)")

            warningMin != null && value < warningMin ->
                ThresholdViolation(SafetySeverity.HIGH, "Below warning minimum: $value $unit (min: $warningMin)")

            warningMax != null && value > warningMax ->
                ThresholdViolation(SafetySeverity.HIGH, "Above warning maximum: $value $unit (max: $warningMax)")

            else -> null
        }
    }
}

data class ThresholdViolation(
    val severity: SafetySeverity,
    val message: String
)

/**
 * IoT device registration.
 */
data class IoTDevice(
    val deviceId: String,
    val name: String,
    val sensorTypes: List<SensorType>,
    val locationId: Long,
    val locationType: LocationType,
    val status: DeviceStatus,
    val lastSeen: Instant?,
    val batteryLevel: Int?,
    val firmwareVersion: String?
)

enum class DeviceStatus {
    ONLINE,
    OFFLINE,
    LOW_BATTERY,
    MAINTENANCE,
    ERROR
}

/**
 * Geofenced zone for personnel tracking.
 */
data class GeoFence(
    val id: String,
    val name: String,
    val locationId: Long,
    val zoneType: ZoneType,
    val coordinates: List<GeoCoordinate>,
    val authorizedRoles: List<String>,  // Which roles can enter
    val requiresEscort: Boolean,
    val maxOccupancy: Int?
)

data class GeoCoordinate(
    val latitude: Double,
    val longitude: Double,
    val altitude: Double? = null
)

enum class ZoneType {
    RESTRICTED,      // Danger - authorization required
    AUTHORIZED_ONLY, // Only specific personnel
    MUSTER_POINT,    // Emergency assembly
    EXCLUSION,       // No entry zone
    WORK_ZONE        // Active work area
}

/**
 * Personnel location tracking.
 */
data class PersonnelLocation(
    val userId: Long,
    val deviceId: String,
    val coordinate: GeoCoordinate,
    val timestamp: Instant,
    val accuracy: Double,  // GPS accuracy in meters
    val isInRestrictedZone: Boolean = false,
    val currentZone: String? = null
)

data class ZoneViolation(
    val zoneId: String,
    val zoneName: String,
    val zoneType: ZoneType,
    val isAuthorized: Boolean
)

data class ActiveAlert(
    val sensorId: String,
    val sensorType: SensorType,
    val severity: com.zama.safeops.modules.safety.domain.model.SafetySeverity,
    val message: String,
    val timestamp: Instant
)
