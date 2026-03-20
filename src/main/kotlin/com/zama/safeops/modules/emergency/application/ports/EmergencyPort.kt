/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 */

package com.zama.safeops.modules.emergency.application.ports

import com.zama.safeops.modules.emergency.domain.model.*
import com.zama.safeops.modules.tenant.domain.valueobjects.TenantId

/**
 * Port for emergency data persistence.
 */
interface EmergencyPort {
    fun save(emergency: Emergency): Emergency
    fun findById(id: EmergencyId): Emergency?
    fun findActive(tenantId: TenantId): List<Emergency>
    fun saveMusterAttendance(attendance: MusterAttendance): MusterAttendance
    fun saveDrill(drill: EmergencyDrill): EmergencyDrill
    fun findUpcomingDrills(tenantId: TenantId): List<EmergencyDrill>
    fun getEmergencyContacts(tenantId: TenantId): List<EmergencyContact>
}
