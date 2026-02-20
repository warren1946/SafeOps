/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 * Permission granted for use, modification, and distribution with attribution.
 * No warranty provided.
 */

package com.zama.safeops.modules.inspections.infrastructure.persistence.jpa.entities

import com.zama.safeops.modules.inspections.domain.model.InspectionStatus
import com.zama.safeops.modules.inspections.domain.model.InspectionTargetType
import jakarta.persistence.*
import java.time.Instant

@Entity
@Table(name = "inspections")
class InspectionJpaEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false)
    val title: String,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val targetType: InspectionTargetType,

    @Column(nullable = false)
    val targetId: Long,

    @Column(nullable = false)
    val inspectorId: Long,

    @Column(nullable = true)
    val assignedReviewerId: Long?,

    @Column(nullable = true)
    val reviewerComments: String?,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val status: InspectionStatus,

    @Column(nullable = false)
    val createdAt: Instant,

    @Column(nullable = false)
    val updatedAt: Instant
)