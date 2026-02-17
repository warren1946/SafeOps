/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 * Permission granted for use, modification, and distribution with attribution.
 * No warranty provided.
 */

package com.zama.safeops.modules.core.application.services

import com.zama.safeops.modules.core.application.exceptions.NotFoundException
import com.zama.safeops.modules.core.application.ports.MinePort
import com.zama.safeops.modules.core.domain.model.Mine
import com.zama.safeops.modules.core.domain.valueobjects.*
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class MineService(
    private val minePort: MinePort
) {

    @Transactional
    fun createMine(name: String, code: String): Mine {
        val mine = Mine(
            id = MineId(1),
            name = MineName(name),
            code = MineCode(code)
        )
        return minePort.save(mine)
    }

    @Transactional(readOnly = true)
    fun listMines(): List<Mine> = minePort.findAll()

    @Transactional(readOnly = true)
    fun getMine(id: Long): Mine =
        minePort.findById(MineId(id)) ?: throw NotFoundException("Mine $id not found")
}