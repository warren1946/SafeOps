/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 * Permission granted for use, modification, and distribution with attribution.
 * No warranty provided.
 */

package com.zama.safeops.modules.inspections.infrastructure.persistence.jpa.entities

import com.zama.safeops.modules.inspections.domain.model.InspectionItemStatus
import jakarta.persistence.*
import java.time.Instant

@Entity
@Table(name = "inspection_items")
class InspectionItemJpaEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false)
    val inspectionId: Long,

    @Column(nullable = false)
    val title: String,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val status: InspectionItemStatus,

    @Column(nullable = true)
    val comment: String? = null,

    @Column(nullable = false)
    val createdAt: Instant
)