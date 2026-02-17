/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 * Permission granted for use, modification, and distribution with attribution.
 * No warranty provided.
 */

package com.zama.safeops.modules.core.infrastructure.persistence.jpa.repository

import com.zama.safeops.modules.core.infrastructure.persistence.jpa.entities.MineJpaEntity
import org.springframework.data.jpa.repository.JpaRepository

interface SpringDataMineRepository : JpaRepository<MineJpaEntity, Long>