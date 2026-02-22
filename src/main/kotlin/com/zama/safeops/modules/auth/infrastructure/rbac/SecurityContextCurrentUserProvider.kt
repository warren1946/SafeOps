/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 * Permission granted for use, modification, and distribution with attribution.
 * No warranty provided.
 */

package com.zama.safeops.modules.auth.infrastructure.rbac

import com.zama.safeops.modules.auth.application.ports.UserPort
import com.zama.safeops.modules.auth.domain.model.User
import com.zama.safeops.modules.auth.domain.valueobjects.UserId
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component

@Component
class SecurityContextCurrentUserProvider(
    private val userPort: UserPort
) : CurrentUserProvider {

    override fun getCurrentUser(): User? {
        val authentication = SecurityContextHolder.getContext().authentication
            ?: return null

        val userId = when (val principal = authentication.principal) {
            is Long -> principal
            is String -> principal.toLongOrNull()
            else -> null
        } ?: return null

        return userPort.findById(UserId(userId))
    }
}