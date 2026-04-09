/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 * Permission granted for use, modification, and distribution with attribution.
 * No warranty provided.
 */

package com.zama.safeops.modules.core.api.dto

import com.zama.safeops.config.validation.Sanitized
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

private const val MAX_NAME_LENGTH = 100
private const val MAX_CODE_LENGTH = 20

data class CreateMineRequest(
    @field:NotBlank(message = "Name is required")
    @field:Size(max = MAX_NAME_LENGTH, message = "Name must not exceed $MAX_NAME_LENGTH characters")
    @field:Sanitized(maxLength = MAX_NAME_LENGTH, allowSpaces = true)
    val name: String,

    @field:NotBlank(message = "Code is required")
    @field:Size(min = 3, max = MAX_CODE_LENGTH, message = "Code must be 3-$MAX_CODE_LENGTH characters")
    @field:Pattern(
        regexp = "^[A-Z0-9_-]+$",
        message = "Code must contain only uppercase letters, numbers, underscores, and hyphens"
    )
    val code: String
)

data class MineResponse(
    val id: Long,
    val name: String,
    val code: String
)

data class CreateSiteRequest(
    @field:NotBlank(message = "Name is required")
    @field:Size(max = MAX_NAME_LENGTH, message = "Name must not exceed $MAX_NAME_LENGTH characters")
    @field:Sanitized(maxLength = MAX_NAME_LENGTH, allowSpaces = true)
    val name: String,

    @field:NotBlank(message = "Mine ID is required")
    @field:Min(1, message = "Mine ID must be positive")
    val mineId: Long
)

data class SiteResponse(
    val id: Long,
    val name: String,
    val mineId: Long
)

data class CreateShaftRequest(
    @field:NotBlank(message = "Name is required")
    @field:Size(max = MAX_NAME_LENGTH, message = "Name must not exceed $MAX_NAME_LENGTH characters")
    @field:Sanitized(maxLength = MAX_NAME_LENGTH, allowSpaces = true)
    val name: String,

    @field:NotBlank(message = "Site ID is required")
    @field:Min(1, message = "Site ID must be positive")
    val siteId: Long
)

data class ShaftResponse(
    val id: Long,
    val name: String,
    val siteId: Long
)

data class CreateAreaRequest(
    @field:NotBlank(message = "Name is required")
    @field:Size(max = MAX_NAME_LENGTH, message = "Name must not exceed $MAX_NAME_LENGTH characters")
    @field:Sanitized(maxLength = MAX_NAME_LENGTH, allowSpaces = true)
    val name: String,

    @field:NotBlank(message = "Shaft ID is required")
    @field:Min(1, message = "Shaft ID must be positive")
    val shaftId: Long
)

data class AreaResponse(
    val id: Long,
    val name: String,
    val shaftId: Long
)
