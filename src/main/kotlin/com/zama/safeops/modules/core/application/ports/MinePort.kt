/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 * Permission granted for use, modification, and distribution with attribution.
 * No warranty provided.
 */

package com.zama.safeops.modules.core.application.ports

import com.zama.safeops.modules.core.domain.model.Mine
import com.zama.safeops.modules.core.domain.valueobjects.MineId

interface MinePort {
    fun save(mine: Mine): Mine
    fun findAll(): List<Mine>
    fun findById(id: MineId): Mine?
    fun exists(id: MineId): Boolean
}