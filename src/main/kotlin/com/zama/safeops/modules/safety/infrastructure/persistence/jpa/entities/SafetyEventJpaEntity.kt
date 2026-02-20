/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 * Permission granted for use, modification, and distribution with attribution.
 * No warranty provided.
 */

package com.zama.safeops.modules.safety.infrastructure.persistence.jpa.entities

import com.zama.safeops.modules.safety.domain.model.SafetyEventType
import com.zama.safeops.modules.safety.domain.model.SafetyLocationType
import com.zama.safeops.modules.safety.domain.model.SafetySeverity
import jakarta.persistence.*
import java.time.Instant

@Entity
@Table(name = "safety_events")
class SafetyEventJpaEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val type: SafetyEventType,

    @Column(nullable = false)
    val description: String,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val severity: SafetySeverity,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val locationType: SafetyLocationType,

    @Column(nullable = false)
    val locationId: Long,

    @Column(nullable = false)
    val reporterId: Long,

    @Column(nullable = false)
    val createdAt: Instant
)