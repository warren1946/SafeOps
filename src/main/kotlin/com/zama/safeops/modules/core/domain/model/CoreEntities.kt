/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 * Permission granted for use, modification, and distribution with attribution.
 * No warranty provided.
 */

package com.zama.safeops.modules.core.domain.model

import com.zama.safeops.modules.core.domain.valueobjects.AreaId
import com.zama.safeops.modules.core.domain.valueobjects.AreaName
import com.zama.safeops.modules.core.domain.valueobjects.MineCode
import com.zama.safeops.modules.core.domain.valueobjects.MineId
import com.zama.safeops.modules.core.domain.valueobjects.MineName
import com.zama.safeops.modules.core.domain.valueobjects.ShaftId
import com.zama.safeops.modules.core.domain.valueobjects.ShaftName
import com.zama.safeops.modules.core.domain.valueobjects.SiteId
import com.zama.safeops.modules.core.domain.valueobjects.SiteName

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