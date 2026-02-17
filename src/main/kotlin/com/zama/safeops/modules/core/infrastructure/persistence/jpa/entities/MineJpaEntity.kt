/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 * Permission granted for use, modification, and distribution with attribution.
 * No warranty provided.
 */

package com.zama.safeops.modules.core.infrastructure.persistence.jpa.entities

import com.zama.safeops.modules.core.domain.model.Mine
import com.zama.safeops.modules.core.domain.valueobjects.MineCode
import com.zama.safeops.modules.core.domain.valueobjects.MineId
import com.zama.safeops.modules.core.domain.valueobjects.MineName
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "mine")
class MineJpaEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private val id: Long? = null,

    @Column(nullable = false)
    private val name: String,

    @Column(nullable = false, unique = true)
    private val code: String

) {

    fun toDomain(): Mine {
        requireNotNull(id) { "MineJpaEntity must be persisted before converting to domain" }

        return Mine(
            id = MineId(id),
            name = MineName(name),
            code = MineCode(code)
        )
    }

    companion object {
        fun fromDomain(mine: Mine) = MineJpaEntity(
            id = mine.id.value,
            name = mine.name.value,
            code = mine.code.value
        )
    }
}