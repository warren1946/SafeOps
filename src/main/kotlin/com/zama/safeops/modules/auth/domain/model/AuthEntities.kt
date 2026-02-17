/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 * Permission granted for use, modification, and distribution with attribution.
 * No warranty provided.
 */

package com.zama.safeops.modules.auth.domain.model

import com.zama.safeops.modules.auth.domain.valueobjects.*

data class Role(
    val id: RoleId,
    val name: RoleName
)

data class User(
    val id: UserId,
    val email: Email,
    val password: PasswordHash,
    val enabled: Boolean,
    val roles: Set<Role>
)