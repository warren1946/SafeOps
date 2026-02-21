/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 * Permission granted for use, modification, and distribution with attribution.
 * No warranty provided.
 */

package com.zama.safeops.modules.hazards.application.ports

import com.zama.safeops.modules.hazards.domain.model.Hazard
import com.zama.safeops.modules.safety.domain.model.SafetyLocationType

interface HazardPort {
    fun create(hazard: Hazard): Hazard
    fun findAll(): List<Hazard>
    fun findById(id: Long): Hazard?
    fun update(hazard: Hazard): Hazard
    fun findActive(limit: Int): List<Hazard>
    fun findByLocation(type: SafetyLocationType, id: Long): List<Hazard>
}