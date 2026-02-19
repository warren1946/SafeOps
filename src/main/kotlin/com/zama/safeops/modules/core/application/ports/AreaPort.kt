/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 * Permission granted for use, modification, and distribution with attribution.
 * No warranty provided.
 */

package com.zama.safeops.modules.core.application.ports

import com.zama.safeops.modules.core.domain.model.Area
import com.zama.safeops.modules.core.domain.valueobjects.AreaId
import com.zama.safeops.modules.core.domain.valueobjects.ShaftId

interface AreaPort {
    fun save(area: Area): Area
    fun findAll(): List<Area>
    fun findById(id: AreaId): Area?
    fun exists(id: AreaId): Boolean
    fun existsShaft(shaftId: ShaftId): Boolean
}