/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 * Permission granted for use, modification, and distribution with attribution.
 * No warranty provided.
 */

package com.zama.safeops.modules.core.application.services

import com.zama.safeops.modules.core.application.ports.AreaPort
import com.zama.safeops.modules.core.domain.exceptions.NotFoundCoreException
import com.zama.safeops.modules.core.domain.model.Area
import com.zama.safeops.modules.core.domain.valueobjects.AreaName
import com.zama.safeops.modules.core.domain.valueobjects.ShaftId
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AreaService(
    private val areaPort: AreaPort
) {

    @Transactional
    fun createArea(name: String, shaftId: Long): Area {
        val shaftIdVo = ShaftId(shaftId)
        if (!areaPort.existsShaft(shaftIdVo)) {
            throw NotFoundCoreException("Shaft $shaftId not found")
        }

        val area = Area(
            name = AreaName(name),
            shaftId = shaftIdVo
        )
        return areaPort.save(area)
    }

    @Transactional(readOnly = true)
    fun listAreas(): List<Area> = areaPort.findAll()
}