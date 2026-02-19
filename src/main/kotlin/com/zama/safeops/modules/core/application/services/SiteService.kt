/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 * Permission granted for use, modification, and distribution with attribution.
 * No warranty provided.
 */

package com.zama.safeops.modules.core.application.services

import com.zama.safeops.modules.core.application.ports.SitePort
import com.zama.safeops.modules.core.domain.exceptions.NotFoundCoreException
import com.zama.safeops.modules.core.domain.model.Site
import com.zama.safeops.modules.core.domain.valueobjects.MineId
import com.zama.safeops.modules.core.domain.valueobjects.SiteId
import com.zama.safeops.modules.core.domain.valueobjects.SiteName
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class SiteService(
    private val sitePort: SitePort
) {

    @Transactional
    fun createSite(name: String, mineId: Long): Site {
        val mineIdVo = MineId(mineId)
        if (!sitePort.existsMine(mineIdVo)) {
            throw NotFoundCoreException("Mine $mineId not found")
        }

        val site = Site(
            id = SiteId(1),
            name = SiteName(name),
            mineId = mineIdVo
        )
        return sitePort.save(site)
    }

    @Transactional(readOnly = true)
    fun listSites(): List<Site> = sitePort.findAll()
}