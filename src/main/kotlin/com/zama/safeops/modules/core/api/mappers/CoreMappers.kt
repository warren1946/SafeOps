/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 * Permission granted for use, modification, and distribution with attribution.
 * No warranty provided.
 */

package com.zama.safeops.modules.core.api.mappers

import com.zama.safeops.modules.core.api.dto.*
import com.zama.safeops.modules.core.domain.model.*

fun Mine.toResponse() = MineResponse(
    id = id.value,
    name = name.value,
    code = code.value
)

fun Site.toResponse() = SiteResponse(
    id = id.value,
    name = name.value,
    mineId = mineId.value
)

fun Shaft.toResponse() = ShaftResponse(
    id = id.value,
    name = name.value,
    siteId = siteId.value
)

fun Area.toResponse() = AreaResponse(
    id = id.value,
    name = name.value,
    shaftId = shaftId.value
)