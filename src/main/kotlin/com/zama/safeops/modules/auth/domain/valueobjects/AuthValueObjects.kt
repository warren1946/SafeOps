/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 * Permission granted for use, modification, and distribution with attribution.
 * No warranty provided.
 */

package com.zama.safeops.modules.auth.domain.valueobjects

@JvmInline
value class UserId(val value: Long)

@JvmInline
value class RoleId(val value: Long)

@JvmInline
value class Email(val value: String)

@JvmInline
value class PasswordHash(val value: String) {
    init {
        require(value.isNotBlank()) { "Password hash cannot be blank" }
    }
}

@JvmInline
value class RoleName(val value: String)