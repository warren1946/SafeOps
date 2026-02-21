/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 * Permission granted for use, modification, and distribution with attribution.
 * No warranty provided.
 */

package com.zama.safeops.modules.hazards.infrastructure.persistence.entities

import com.zama.safeops.modules.hazards.domain.model.HazardStatus
import com.zama.safeops.modules.safety.domain.model.SafetyLocationType
import jakarta.persistence.*
import java.time.Instant

@Entity
@Table(name = "hazards")
class HazardJpaEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false)
    val title: String,

    @Column(nullable = false)
    val description: String,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val status: HazardStatus,

    val assignedTo: Long? = null,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val locationType: SafetyLocationType,

    @Column(nullable = false)
    val locationId: Long,

    @Column(nullable = false)
    val createdAt: Instant,

    @Column(nullable = false)
    val updatedAt: Instant
)