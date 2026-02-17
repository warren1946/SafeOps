/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 * Permission granted for use, modification, and distribution with attribution.
 * No warranty provided.
 */

package com.zama.safeops.modules.core.api

data class CreateMineRequest(
    val name: String,
    val code: String
)

data class CreateSiteRequest(
    val name: String,
    val mineId: Long
)

data class CreateShaftRequest(
    val name: String,
    val siteId: Long
)

data class CreateAreaRequest(
    val name: String,
    val shaftId: Long
)