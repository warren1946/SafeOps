/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 * Permission granted for use, modification, and distribution with attribution.
 * No warranty provided.
 */

package com.zama.safeops.modules.core.api.controllers

import com.zama.safeops.modules.core.api.dto.CreateMineRequest
import com.zama.safeops.modules.core.api.dto.MineResponse
import com.zama.safeops.modules.core.api.mappers.toResponse
import com.zama.safeops.modules.core.application.services.MineService
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/core/mines")
class MineController(
    private val mineService: MineService
) {

    @PostMapping
    fun create(@Valid @RequestBody req: CreateMineRequest): MineResponse =
        mineService.createMine(req.name, req.code).toResponse()

    @GetMapping
    fun list(): List<MineResponse> =
        mineService.listMines().map { it.toResponse() }

    @GetMapping("/{id}")
    fun get(@PathVariable id: Long): MineResponse =
        mineService.getMine(id).toResponse()
}