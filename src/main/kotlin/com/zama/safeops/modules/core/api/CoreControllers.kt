/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 * Permission granted for use, modification, and distribution with attribution.
 * No warranty provided.
 */

package com.zama.safeops.modules.core.api

import com.zama.safeops.modules.core.application.*
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/core/mines")
class MineController(
    private val mineService: MineService
) {

    @PostMapping
    fun create(@RequestBody req: CreateMineRequest) =
        mineService.createMine(req.name, req.code)

    @GetMapping
    fun list() = mineService.listMines()
}

@RestController
@RequestMapping("/api/core/sites")
class SiteController(
    private val siteService: SiteService
) {

    @PostMapping
    fun create(@RequestBody req: CreateSiteRequest) =
        siteService.createSite(req.name, req.mineId)

    @GetMapping
    fun list() = siteService.listSites()
}

@RestController
@RequestMapping("/api/core/shafts")
class ShaftController(
    private val shaftService: ShaftService
) {

    @PostMapping
    fun create(@RequestBody req: CreateShaftRequest) =
        shaftService.createShaft(req.name, req.siteId)

    @GetMapping
    fun list() = shaftService.listShafts()
}

@RestController
@RequestMapping("/api/core/areas")
class AreaController(
    private val areaService: AreaService
) {

    @PostMapping
    fun create(@RequestBody req: CreateAreaRequest) =
        areaService.createArea(req.name, req.shaftId)

    @GetMapping
    fun list() = areaService.listAreas()
}