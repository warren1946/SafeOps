/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 * Permission granted for use, modification, and distribution with attribution.
 * No warranty provided.
 */

package com.zama.safeops.modules.core.application.ports

import com.zama.safeops.modules.core.domain.model.Site
import com.zama.safeops.modules.core.domain.valueobjects.SiteId
import com.zama.safeops.modules.core.domain.valueobjects.MineId

interface SitePort {
    fun save(site: Site): Site
    fun findAll(): List<Site>
    fun exists(id: SiteId): Boolean
    fun existsMine(mineId: MineId): Boolean
}