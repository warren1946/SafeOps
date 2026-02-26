/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 * Permission granted for use, modification, and distribution with attribution.
 * No warranty provided.
 */

package com.zama.safeops.modules.templates.infrastructure.persistence.jpa.entities

import com.zama.safeops.modules.templates.domain.model.QuestionType
import jakarta.persistence.*
import java.time.Instant

@Entity
@Table(name = "template_questions")
class TemplateQuestionJpaEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false)
    val templateId: Long,

    @Column(nullable = false)
    val text: String,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val type: QuestionType,

    @Column(nullable = false)
    val required: Boolean,

    @Column(nullable = false)
    val orderIndex: Int,

    @Column(columnDefinition = "jsonb")
    val options: String? = null,

    @Column(nullable = false)
    val createdAt: Instant = Instant.now(),

    @Column(nullable = false)
    val updatedAt: Instant = Instant.now()
)