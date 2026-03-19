/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 */

package com.zama.safeops.modules.tenant.infrastructure.persistence

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface TenantJpaRepository : JpaRepository<TenantJpaEntity, Long> {
    fun findBySlug(slug: String): TenantJpaEntity?
    fun existsBySlug(slug: String): Boolean
    fun findAllByStatus(status: String): List<TenantJpaEntity>
}
