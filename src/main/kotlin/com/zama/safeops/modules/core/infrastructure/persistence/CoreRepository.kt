/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 * Permission granted for use, modification, and distribution with attribution.
 * No warranty provided.
 */

package com.zama.safeops.modules.core.infrastructure.persistence

import org.springframework.data.jpa.repository.JpaRepository

interface MineRepository : JpaRepository<MineJpaEntity, Long>
interface SiteRepository : JpaRepository<SiteJpaEntity, Long>
interface ShaftRepository : JpaRepository<ShaftJpaEntity, Long>
interface AreaRepository : JpaRepository<AreaJpaEntity, Long>