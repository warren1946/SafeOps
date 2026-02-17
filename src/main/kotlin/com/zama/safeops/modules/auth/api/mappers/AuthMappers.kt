/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 * Permission granted for use, modification, and distribution with attribution.
 * No warranty provided.
 */

package com.zama.safeops.modules.auth.api.mappers

import com.zama.safeops.modules.auth.api.dto.UserResponse
import com.zama.safeops.modules.auth.domain.model.User

fun User.toResponse(): UserResponse? = id?.value?.let {
    UserResponse(
        id = it,
        email = email.value,
        enabled = enabled,
        roles = roles.map { it.name.value }.toSet()
    )
}