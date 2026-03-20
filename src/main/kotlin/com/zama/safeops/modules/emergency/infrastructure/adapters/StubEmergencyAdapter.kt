/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 */

package com.zama.safeops.modules.emergency.infrastructure.adapters

import com.zama.safeops.modules.emergency.application.ports.EmergencyPort
import com.zama.safeops.modules.emergency.domain.model.*
import com.zama.safeops.modules.tenant.domain.valueobjects.TenantId
import org.springframework.stereotype.Component

/**
 * Stub implementation of EmergencyPort.
 * Returns empty results until a real implementation is configured.
 */
@Component
class StubEmergencyAdapter : EmergencyPort {

    override fun save(emergency: Emergency): Emergency = emergency

    override fun findById(id: EmergencyId): Emergency? = null

    override fun findActive(tenantId: TenantId): List<Emergency> = emptyList()

    override fun saveMusterAttendance(attendance: MusterAttendance): MusterAttendance = attendance

    override fun saveDrill(drill: EmergencyDrill): EmergencyDrill = drill

    override fun findUpcomingDrills(tenantId: TenantId): List<EmergencyDrill> = emptyList()

    override fun getEmergencyContacts(tenantId: TenantId): List<EmergencyContact> = emptyList()
}
