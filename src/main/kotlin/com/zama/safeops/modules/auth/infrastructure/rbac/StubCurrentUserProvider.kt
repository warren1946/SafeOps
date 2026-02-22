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
import org.springframework.stereotype.Component

@Component
class StubCurrentUserProvider(
    private val userPort: UserPort
) : CurrentUserProvider {

    override fun getCurrentUser(): User? {
        // TODO: replace with real auth integration
        // For now, always return user with ID 1 if exists
        return userPort.findById(UserId(1))
    }
}