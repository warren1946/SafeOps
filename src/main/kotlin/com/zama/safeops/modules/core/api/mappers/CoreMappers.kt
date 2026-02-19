/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 * Permission granted for use, modification, and distribution with attribution.
 * No warranty provided.
 */

package com.zama.safeops.modules.core.api.mappers

import com.zama.safeops.modules.core.api.dto.AreaResponse
import com.zama.safeops.modules.core.api.dto.MineResponse
import com.zama.safeops.modules.core.api.dto.ShaftResponse
import com.zama.safeops.modules.core.api.dto.SiteResponse
import com.zama.safeops.modules.core.domain.model.Area
import com.zama.safeops.modules.core.domain.model.Mine
import com.zama.safeops.modules.core.domain.model.Shaft
import com.zama.safeops.modules.core.domain.model.Site

fun Mine.toResponse() = MineResponse(
    id = id?.value ?: error("Mine ID must not be null"),
    name = name.value,
    code = code.value
)

fun Site.toResponse() = SiteResponse(
    id = id?.value ?: error("Site ID must not be null"),
    name = name.value,
    mineId = mineId.value
)

fun Shaft.toResponse() = ShaftResponse(
    id = id?.value ?: error("Shaft ID must not be null"),
    name = name.value,
    siteId = siteId.value
)

fun Area.toResponse() = AreaResponse(
    id = id?.value ?: error("Area ID must not be null"),
    name = name.value,
    shaftId = shaftId.value
)