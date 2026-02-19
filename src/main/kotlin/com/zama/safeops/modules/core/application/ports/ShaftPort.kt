/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 * Permission granted for use, modification, and distribution with attribution.
 * No warranty provided.
 */

package com.zama.safeops.modules.core.application.ports

import com.zama.safeops.modules.core.domain.model.Shaft
import com.zama.safeops.modules.core.domain.valueobjects.ShaftId
import com.zama.safeops.modules.core.domain.valueobjects.SiteId

interface ShaftPort {
    fun save(shaft: Shaft): Shaft
    fun findAll(): List<Shaft>
    fun findById(id: ShaftId): Shaft?
    fun exists(id: ShaftId): Boolean
    fun existsSite(siteId: SiteId): Boolean
}