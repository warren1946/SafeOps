/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 * Permission granted for use, modification, and distribution with attribution.
 * No warranty provided.
 */

package com.zama.safeops.modules.templates.infrastructure.persistence.jpa.entities

import jakarta.persistence.*
import java.time.Instant

@Entity
@Table(name = "templates")
class TemplateJpaEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false)
    val name: String,

    @Column(nullable = false)
    val description: String,

    val category: String? = null,

    val createdBy: Long? = null,

    @Column(nullable = false)
    val usageCount: Int,

    @Column(nullable = false)
    val createdAt: Instant,

    @Column(nullable = false)
    val updatedAt: Instant
)