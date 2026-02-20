/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 * Permission granted for use, modification, and distribution with attribution.
 * No warranty provided.
 */

package com.zama.safeops.modules.safety.infrastructure.persistence.jpa.adapters

import com.zama.safeops.modules.safety.application.ports.SafetyEventPort
import com.zama.safeops.modules.safety.domain.model.SafetyEvent
import com.zama.safeops.modules.safety.domain.valueobjects.SafetyEventId
import com.zama.safeops.modules.safety.infrastructure.persistence.jpa.entities.SafetyEventJpaEntity
import com.zama.safeops.modules.safety.infrastructure.persistence.jpa.repositories.SpringDataSafetyEventRepository
import org.springframework.stereotype.Component
import java.time.Instant

@Component
class SafetyEventJpaAdapter(
    private val repo: SpringDataSafetyEventRepository
) : SafetyEventPort {

    override fun create(event: SafetyEvent): SafetyEvent =
        repo.save(event.toEntity()).toDomain()

    override fun findById(id: SafetyEventId): SafetyEvent? =
        repo.findById(id.value).orElse(null)?.toDomain()

    override fun findByPeriod(start: Instant, end: Instant): List<SafetyEvent> =
        repo.findByCreatedAtBetween(start, end).map { it.toDomain() }
}

private fun SafetyEvent.toEntity() = SafetyEventJpaEntity(
    id = id?.value,
    type = type,
    description = description,
    severity = severity,
    locationType = locationType,
    locationId = locationId,
    reporterId = reporterId,
    createdAt = createdAt
)

private fun SafetyEventJpaEntity.toDomain() = SafetyEvent(
    id = SafetyEventId(id!!),
    type = type,
    description = description,
    severity = severity,
    locationType = locationType,
    locationId = locationId,
    reporterId = reporterId,
    createdAt = createdAt
)