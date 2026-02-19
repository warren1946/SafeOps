/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 * Permission granted for use, modification, and distribution with attribution.
 * No warranty provided.
 */

package com.zama.safeops.modules.core.application.services

import com.zama.safeops.modules.core.application.ports.ShaftPort
import com.zama.safeops.modules.core.domain.exceptions.NotFoundCoreException
import com.zama.safeops.modules.core.domain.model.Shaft
import com.zama.safeops.modules.core.domain.valueobjects.ShaftId
import com.zama.safeops.modules.core.domain.valueobjects.ShaftName
import com.zama.safeops.modules.core.domain.valueobjects.SiteId
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ShaftService(
    private val shaftPort: ShaftPort
) {

    @Transactional
    fun createShaft(name: String, siteId: Long): Shaft {
        val siteIdVo = SiteId(siteId)
        if (!shaftPort.existsSite(siteIdVo)) {
            throw NotFoundCoreException("Site $siteId not found")
        }

        val shaft = Shaft(
            name = ShaftName(name),
            siteId = siteIdVo
        )
        return shaftPort.save(shaft)
    }

    @Transactional(readOnly = true)
    fun listShafts(): List<Shaft> = shaftPort.findAll()

    @Transactional(readOnly = true)
    fun getShaft(id: Long): Shaft = shaftPort.findById(ShaftId(id)) ?: throw NotFoundCoreException("Shaft $id not found")
}