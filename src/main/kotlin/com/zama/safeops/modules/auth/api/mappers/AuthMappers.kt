/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 * Permission granted for use, modification, and distribution with attribution.
 * No warranty provided.
 */

package com.zama.safeops.modules.auth.api.mappers

import com.zama.safeops.modules.auth.api.dto.UserResponse
import com.zama.safeops.modules.auth.domain.model.User

fun User.toResponse(): UserResponse =
    UserResponse(
        id = this.id!!.value,
        email = this.email.value,
        enabled = this.enabled,
        roles = this.roles.map { it.name.value }.toSet()
    )