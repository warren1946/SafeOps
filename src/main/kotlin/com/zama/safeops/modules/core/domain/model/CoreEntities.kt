/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 * Permission granted for use, modification, and distribution with attribution.
 * No warranty provided.
 */

package com.zama.safeops.modules.core.domain.model

import com.zama.safeops.modules.core.domain.valueobjects.*

data class Mine(
    val id: MineId? = null,
    val name: MineName,
    val code: MineCode
)

data class Site(
    val id: SiteId? = null,
    val name: SiteName,
    val mineId: MineId
)

data class Shaft(
    val id: ShaftId? = null,
    val name: ShaftName,
    val siteId: SiteId
)

data class Area(
    val id: AreaId? = null,
    val name: AreaName,
    val shaftId: ShaftId
)