/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 * Permission granted for use, modification, and distribution with attribution.
 * No warranty provided.
 */

package com.zama.safeops.modules.core.infrastructure.persistence

import com.zama.safeops.modules.core.domain.*
import jakarta.persistence.*

@Entity
@Table(name = "shaft")
class ShaftJpaEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private val id: Long? = null,

    @Column(nullable = false)
    private val name: String,

    @Column(name = "site_id", nullable = false)
    private val siteId: Long

) {

    fun toDomain(): Shaft {
        requireNotNull(id) { "ShaftJpaEntity must be persisted before converting to domain" }

        return Shaft(
            id = ShaftId(id),
            name = ShaftName(name),
            siteId = SiteId(siteId)
        )
    }

    companion object {
        fun fromDomain(shaft: Shaft) = ShaftJpaEntity(
            id = shaft.id.value,
            name = shaft.name.value,
            siteId = shaft.siteId.value
        )
    }
}