/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 * Permission granted for use, modification, and distribution with attribution.
 * No warranty provided.
 */

package com.zama.safeops.modules.hazards.infrastructure.persistence.entities

import com.zama.safeops.modules.hazards.domain.model.HazardStatus
import jakarta.persistence.*
import java.time.Instant

@Entity
@Table(name = "hazards")
class HazardJpaEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    val title: String,
    val description: String,

    @Enumerated(EnumType.STRING)
    val status: HazardStatus,

    val assignedTo: Long? = null,

    @Column(nullable = false)
    val createdAt: Instant,

    @Column(nullable = false)
    val updatedAt: Instant
)