/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 * Permission granted for use, modification, and distribution with attribution.
 * No warranty provided.
 */

package com.zama.safeops.modules.core.domain.valueobjects

@JvmInline
value class MineId(val value: Long) {
    init { require(value > 0) { "Mine ID must be positive" } }
}

@JvmInline
value class SiteId(val value: Long) {
    init { require(value > 0) { "Site ID must be positive" } }
}

@JvmInline
value class ShaftId(val value: Long) {
    init { require(value > 0) { "Shaft ID must be positive" } }
}

@JvmInline
value class AreaId(val value: Long) {
    init { require(value > 0) { "Area ID must be positive" } }
}

@JvmInline
value class MineName(val value: String) {
    init { require(value.isNotBlank()) { "Mine name cannot be blank" } }
}

@JvmInline
value class MineCode(val value: String) {
    init {
        require(value.matches(Regex("^[A-Z0-9_-]{3,20}$"))) {
            "Mine code must be 3–20 chars, uppercase letters, digits, _ or -"
        }
    }
}

@JvmInline
value class SiteName(val value: String) {
    init { require(value.isNotBlank()) { "Site name cannot be blank" } }
}

@JvmInline
value class ShaftName(val value: String) {
    init { require(value.isNotBlank()) { "Shaft name cannot be blank" } }
}

@JvmInline
value class AreaName(val value: String) {
    init { require(value.isNotBlank()) { "Area name cannot be blank" } }
}