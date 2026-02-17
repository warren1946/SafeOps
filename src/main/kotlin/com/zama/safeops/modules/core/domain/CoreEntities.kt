/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 * Permission granted for use, modification, and distribution with attribution.
 * No warranty provided.
 */

package com.zama.safeops.modules.core.domain

data class Mine(
    val id: MineId,
    val name: MineName,
    val code: MineCode
)

data class Site(
    val id: SiteId,
    val name: SiteName,
    val mineId: MineId
)

data class Shaft(
    val id: ShaftId,
    val name: ShaftName,
    val siteId: SiteId
)

data class Area(
    val id: AreaId,
    val name: AreaName,
    val shaftId: ShaftId
)