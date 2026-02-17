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
@Table(name = "area")
class AreaJpaEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private val id: Long? = null,

    @Column(nullable = false)
    private val name: String,

    @Column(name = "shaft_id", nullable = false)
    private val shaftId: Long

) {

    fun toDomain(): Area {
        requireNotNull(id) { "AreaJpaEntity must be persisted before converting to domain" }

        return Area(
            id = AreaId(id),
            name = AreaName(name),
            shaftId = ShaftId(shaftId)
        )
    }

    companion object {
        fun fromDomain(area: Area) = AreaJpaEntity(
            id = area.id.value,
            name = area.name.value,
            shaftId = area.shaftId.value
        )
    }
}