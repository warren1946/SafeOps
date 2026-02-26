/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 * Permission granted for use, modification, and distribution with attribution.
 * No warranty provided.
 */

package com.zama.safeops.modules.templates.application.services

import com.zama.safeops.modules.templates.api.dto.TemplateFilterCriteria
import com.zama.safeops.modules.templates.api.dto.TemplateSortField
import com.zama.safeops.modules.templates.application.ports.TemplatePort
import com.zama.safeops.modules.templates.domain.model.Template
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class TemplateQueryService(
    private val templatePort: TemplatePort
) {

    @Transactional(readOnly = true)
    fun getFiltered(criteria: TemplateFilterCriteria): List<Template> {
        val all = templatePort.findAll()

        val filtered = all
            .filter { matchesSearch(criteria, it) }
            .filter { matchesCategory(criteria, it) }

        return sort(criteria, filtered)
    }

    private fun matchesSearch(c: TemplateFilterCriteria, t: Template): Boolean {
        val q = c.search?.trim()?.lowercase() ?: return true
        if (q.isBlank()) return true

        return t.name.value.lowercase().contains(q) ||
                t.description.value.lowercase().contains(q)
    }

    private fun matchesCategory(c: TemplateFilterCriteria, t: Template): Boolean =
        c.category?.let { it == t.category?.value } ?: true

    private fun sort(c: TemplateFilterCriteria, list: List<Template>): List<Template> {
        val comparator = when (c.sortBy) {
            TemplateSortField.NAME ->
                compareBy<Template> { it.name.value.lowercase() }

            TemplateSortField.USAGE ->
                compareBy<Template> { it.usageCount }

            TemplateSortField.CREATED_AT ->
                compareBy<Template> { it.createdAt }
        }

        val sorted = list.sortedWith(comparator)
        return if (c.direction.name == "ASC") sorted else sorted.reversed()
    }
}