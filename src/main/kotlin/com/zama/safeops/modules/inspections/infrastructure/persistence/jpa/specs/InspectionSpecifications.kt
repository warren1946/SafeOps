/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 * Permission granted for use, modification, and distribution with attribution.
 * No warranty provided.
 */

package com.zama.safeops.modules.inspections.infrastructure.persistence.jpa.specs

import com.zama.safeops.modules.inspections.domain.model.InspectionStatus
import com.zama.safeops.modules.inspections.infrastructure.persistence.jpa.entities.InspectionJpaEntity
import com.zama.safeops.modules.safety.domain.model.SafetyLocationType
import jakarta.persistence.criteria.Predicate
import org.springframework.data.jpa.domain.Specification
import java.time.Instant

object InspectionSpecifications {

    fun statusEquals(status: InspectionStatus?): Specification<InspectionJpaEntity> =
        Specification { root, _, cb ->
            if (status == null) cb.conjunction()
            else cb.equal(root.get<InspectionStatus>("status"), status)
        }

    fun dateBetween(from: Instant?, to: Instant?) = Specification<InspectionJpaEntity> { root, _, cb ->
        val field = root.get<Instant>("performedAt")
        val predicates = mutableListOf<Predicate>()

        if (from != null) predicates += cb.greaterThanOrEqualTo(field, from)
        if (to != null) predicates += cb.lessThanOrEqualTo(field, to)

        cb.and(*predicates.toTypedArray())
    }

    fun officerEquals(officerId: Long?) = Specification<InspectionJpaEntity> { root, _, cb ->
        officerId?.let { cb.equal(root.get<Long>("officerId"), it) }
    }

    fun locationEquals(type: SafetyLocationType?, id: Long?) = Specification<InspectionJpaEntity> { root, _, cb ->
        val predicates = mutableListOf<Predicate>()

        type?.let { predicates += cb.equal(root.get<String>("targetType"), it.name) }
        id?.let { predicates += cb.equal(root.get<Long>("targetId"), it) }

        cb.and(*predicates.toTypedArray())
    }

    fun searchLike(query: String?) = Specification<InspectionJpaEntity> { root, _, cb ->
        val q = query?.trim()?.lowercase() ?: return@Specification null

        val title = cb.lower(root.get<String>("title"))
        val description = cb.lower(root.get<String>("description"))

        cb.or(
            cb.like(title, "%$q%"),
            cb.like(description, "%$q%")
        )
    }
}