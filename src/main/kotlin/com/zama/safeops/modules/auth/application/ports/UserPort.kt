/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 * Permission granted for use, modification, and distribution with attribution.
 * No warranty provided.
 */

package com.zama.safeops.modules.auth.application.ports

import com.zama.safeops.modules.auth.domain.model.User
import com.zama.safeops.modules.auth.domain.valueobjects.Email
import com.zama.safeops.modules.auth.domain.valueobjects.UserId

interface UserPort {
    fun save(user: User): User
    fun findByEmail(email: Email): User?
    fun existsByEmail(email: Email): Boolean
    fun findById(id: UserId): User?
    fun existsById(id: UserId): Boolean
    fun findAll(): List<User>
}