/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 * Permission granted for use, modification, and distribution with attribution.
 * No warranty provided.
 */

package com.zama.safeops.modules.core.api.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Positive

data class CreateMineRequest(
    @field:NotBlank
    val name: String,
    @field:NotBlank
    @field:Pattern(regexp = "^[A-Z0-9_-]{3,20}$")
    val code: String
)

data class MineResponse(
    val id: Long,
    val name: String,
    val code: String
)

data class CreateSiteRequest(
    @field:NotBlank
    val name: String,
    @field:Positive
    val mineId: Long
)

data class SiteResponse(
    val id: Long,
    val name: String,
    val mineId: Long
)

data class CreateShaftRequest(
    @field:NotBlank
    val name: String,
    @field:Positive
    val siteId: Long
)

data class ShaftResponse(
    val id: Long,
    val name: String,
    val siteId: Long
)

data class CreateAreaRequest(
    @field:NotBlank
    val name: String,
    @field:Positive
    val shaftId: Long
)

data class AreaResponse(
    val id: Long,
    val name: String,
    val shaftId: Long
)