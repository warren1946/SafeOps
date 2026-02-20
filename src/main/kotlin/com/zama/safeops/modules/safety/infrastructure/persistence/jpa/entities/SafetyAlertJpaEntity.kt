/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 * Permission granted for use, modification, and distribution with attribution.
 * No warranty provided.
 */

package com.zama.safeops.modules.safety.infrastructure.persistence.jpa.entities

import com.zama.safeops.modules.safety.domain.model.SafetyAlertType
import jakarta.persistence.*
import java.time.Instant

@Entity
@Table(name = "safety_alerts")
class SafetyAlertJpaEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false)
    val eventId: Long,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val alertType: SafetyAlertType,

    @Column(nullable = false)
    val message: String,

    @Column(nullable = false)
    val recipientId: Long,

    @Column(nullable = false)
    val acknowledged: Boolean,

    @Column(nullable = true)
    val acknowledgedAt: Instant?,

    @Column(nullable = false)
    val createdAt: Instant
)