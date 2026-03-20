/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 */

package com.zama.safeops.modules.iot.application.ports

import com.zama.safeops.modules.iot.domain.model.*
import com.zama.safeops.modules.tenant.domain.valueobjects.TenantId
import java.time.Instant

/**
 * Port for sensor data persistence.
 */
interface SensorDataPort {
    fun saveReading(reading: SensorReading)
    fun getThreshold(sensorType: SensorType, locationId: Long): SensorThreshold?
    fun getActiveAlerts(tenantId: TenantId): List<ActiveAlert>
    fun getDeviceStatuses(tenantId: TenantId): List<IoTDevice>
    fun getActivePersonnelCount(tenantId: TenantId): Int
    fun getRecentReadings(tenantId: TenantId, limit: Int): List<SensorReading>
    fun getReadingsInRange(sensorId: String, from: Instant, to: Instant): List<SensorReading>
    fun getLastReading(sensorId: String): SensorReading?
    fun updateDeviceStatus(sensorId: String, status: DeviceStatus)
    fun savePersonnelLocation(location: PersonnelLocation)
    fun saveZoneViolation(location: PersonnelLocation, violation: ZoneViolation)
}

interface GeoFencingService {
    fun checkZoneAccess(userId: Long, coordinate: GeoCoordinate): ZoneViolation?
}
