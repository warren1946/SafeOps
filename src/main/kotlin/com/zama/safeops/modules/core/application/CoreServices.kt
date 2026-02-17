/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 * Permission granted for use, modification, and distribution with attribution.
 * No warranty provided.
 */

package com.zama.safeops.modules.core.application

import com.zama.safeops.modules.core.domain.*
import com.zama.safeops.modules.core.infrastructure.persistence.*
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class MineService(
    private val mineRepository: MineRepository
) {

    @Transactional
    fun createMine(name: String, code: String): Mine {
        val domain = Mine(
            id = MineId(1), // temporary, replaced by DB
            name = MineName(name),
            code = MineCode(code)
        )
        val saved = mineRepository.save(MineJpaEntity.fromDomain(domain))
        return saved.toDomain()
    }

    @Transactional(readOnly = true)
    fun listMines(): List<Mine> =
        mineRepository.findAll().map { it.toDomain() }
}

@Service
class SiteService(
    private val siteRepository: SiteRepository
) {

    @Transactional
    fun createSite(name: String, mineId: Long): Site {
        val domain = Site(
            id = SiteId(1),
            name = SiteName(name),
            mineId = MineId(mineId)
        )
        val saved = siteRepository.save(SiteJpaEntity.fromDomain(domain))
        return saved.toDomain()
    }

    @Transactional(readOnly = true)
    fun listSites(): List<Site> =
        siteRepository.findAll().map { it.toDomain() }
}

@Service
class ShaftService(
    private val shaftRepository: ShaftRepository
) {

    @Transactional
    fun createShaft(name: String, siteId: Long): Shaft {
        val domain = Shaft(
            id = ShaftId(1),
            name = ShaftName(name),
            siteId = SiteId(siteId)
        )
        val saved = shaftRepository.save(ShaftJpaEntity.fromDomain(domain))
        return saved.toDomain()
    }

    @Transactional(readOnly = true)
    fun listShafts(): List<Shaft> =
        shaftRepository.findAll().map { it.toDomain() }
}

@Service
class AreaService(
    private val areaRepository: AreaRepository
) {

    @Transactional
    fun createArea(name: String, shaftId: Long): Area {
        val domain = Area(
            id = AreaId(1),
            name = AreaName(name),
            shaftId = ShaftId(shaftId)
        )
        val saved = areaRepository.save(AreaJpaEntity.fromDomain(domain))
        return saved.toDomain()
    }

    @Transactional(readOnly = true)
    fun listAreas(): List<Area> =
        areaRepository.findAll().map { it.toDomain() }
}