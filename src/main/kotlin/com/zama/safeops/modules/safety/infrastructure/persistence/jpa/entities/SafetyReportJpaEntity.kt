/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 * Permission granted for use, modification, and distribution with attribution.
 * No warranty provided.
 */

package com.zama.safeops.modules.safety.infrastructure.persistence.jpa.entities

import jakarta.persistence.*
import java.time.Instant

@Entity
@Table(name = "safety_reports")
class SafetyReportJpaEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false)
    val title: String,

    @Column(nullable = false)
    val periodStart: Instant,

    @Column(nullable = false)
    val periodEnd: Instant,

    @Column(nullable = false)
    val createdBy: Long,

    @Column(nullable = false)
    val createdAt: Instant,

    @Column(nullable = false)
    val summary: String,

    @Column(nullable = false)
    val eventCount: Int,

    @Column(nullable = false)
    val highSeverityCount: Int,

    @Column(nullable = false)
    val criticalSeverityCount: Int
)