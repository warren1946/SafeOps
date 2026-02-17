/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 * Permission granted for use, modification, and distribution with attribution.
 * No warranty provided.
 */

package com.zama.safeops.modules.core.infrastructure.persistence.jpa.entities

import com.zama.safeops.modules.core.domain.model.Site
import com.zama.safeops.modules.core.domain.valueobjects.MineId
import com.zama.safeops.modules.core.domain.valueobjects.SiteId
import com.zama.safeops.modules.core.domain.valueobjects.SiteName
import jakarta.persistence.*

@Entity
@Table(name = "site")
class SiteJpaEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private val id: Long? = null,

    @Column(nullable = false)
    private val name: String,

    @Column(name = "mine_id", nullable = false)
    private val mineId: Long

) {

    fun toDomain(): Site {
        requireNotNull(id) { "SiteJpaEntity must be persisted before converting to domain" }

        return Site(
            id = SiteId(id),
            name = SiteName(name),
            mineId = MineId(mineId)
        )
    }

    companion object {
        fun fromDomain(site: Site) = SiteJpaEntity(
            id = site.id.value,
            name = site.name.value,
            mineId = site.mineId.value
        )
    }
}