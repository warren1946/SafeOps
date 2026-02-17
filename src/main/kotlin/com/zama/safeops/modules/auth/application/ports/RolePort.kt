/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 * Permission granted for use, modification, and distribution with attribution.
 * No warranty provided.
 */

package com.zama.safeops.modules.auth.application.ports

import com.zama.safeops.modules.auth.domain.model.Role
import com.zama.safeops.modules.auth.domain.valueobjects.RoleId
import com.zama.safeops.modules.auth.domain.valueobjects.RoleName

interface RolePort {
    fun save(role: Role): Role
    fun findByName(name: RoleName): Role?
    fun findById(id: RoleId): Role?
    fun findAll(): List<Role>
}