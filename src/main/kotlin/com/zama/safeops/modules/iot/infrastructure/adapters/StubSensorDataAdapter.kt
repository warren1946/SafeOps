/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 */

package com.zama.safeops.modules.iot.infrastructure.adapters

import com.zama.safeops.modules.iot.application.ports.GeoFencingService
import com.zama.safeops.modules.iot.application.ports.SensorDataPort
import com.zama.safeops.modules.iot.domain.model.*
import com.zama.safeops.modules.tenant.domain.valueobjects.TenantId
import org.springframework.stereotype.Component
import java.time.Instant

/**
 * Stub implementation of SensorDataPort and GeoFencingService.
 */
@Component
class StubSensorDataAdapter : SensorDataPort, GeoFencingService {

    override fun saveReading(reading: SensorReading) {}

    override fun getThreshold(sensorType: SensorType, locationId: Long): SensorThreshold? = null

    override fun getActiveAlerts(tenantId: TenantId): List<ActiveAlert> = emptyList()

    override fun getDeviceStatuses(tenantId: TenantId): List<IoTDevice> = emptyList()

    override fun getActivePersonnelCount(tenantId: TenantId): Int = 0

    override fun getRecentReadings(tenantId: TenantId, limit: Int): List<SensorReading> = emptyList()

    override fun getReadingsInRange(sensorId: String, from: Instant, to: Instant): List<SensorReading> = emptyList()

    override fun getLastReading(sensorId: String): SensorReading? = null

    override fun updateDeviceStatus(sensorId: String, status: DeviceStatus) {}

    override fun savePersonnelLocation(location: PersonnelLocation) {}

    override fun saveZoneViolation(location: PersonnelLocation, violation: ZoneViolation) {}

    override fun checkZoneAccess(userId: Long, coordinate: GeoCoordinate): ZoneViolation? = null
}
