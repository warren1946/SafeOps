/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 * Permission granted for use, modification, and distribution with attribution.
 * No warranty provided.
 */

package com.zama.safeops.modules.core.application.services

import com.zama.safeops.modules.core.application.ports.MinePort
import com.zama.safeops.modules.core.domain.exceptions.NotFoundCoreException
import com.zama.safeops.modules.core.domain.model.Mine
import com.zama.safeops.modules.core.domain.valueobjects.MineCode
import com.zama.safeops.modules.core.domain.valueobjects.MineId
import com.zama.safeops.modules.core.domain.valueobjects.MineName
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class MineService(
    private val minePort: MinePort
) {

    @Transactional
    fun createMine(name: String, code: String): Mine {
        val mine = Mine(
            name = MineName(name),
            code = MineCode(code)
        )
        return minePort.save(mine)
    }

    @Transactional(readOnly = true)
    fun listMines(): List<Mine> = minePort.findAll()

    @Transactional(readOnly = true)
    fun getMine(id: Long): Mine =
        minePort.findById(MineId(id)) ?: throw NotFoundCoreException("Mine $id not found")
}