/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 * Permission granted for use, modification, and distribution with attribution.
 * No warranty provided.
 */

package com.zama.safeops.modules.templates.domain.model

import java.time.Instant

data class Template(
    val id: TemplateId? = null,
    val name: TemplateName,
    val description: TemplateDescription,
    val category: TemplateCategory? = null,
    val createdBy: Long? = null,
    val usageCount: Int = 0,
    val createdAt: Instant = Instant.now(),
    val updatedAt: Instant = Instant.now()
)

@JvmInline
value class TemplateId(val value: Long)

@JvmInline
value class TemplateName(val value: String)

@JvmInline
value class TemplateDescription(val value: String)

@JvmInline
value class TemplateCategory(val value: String)