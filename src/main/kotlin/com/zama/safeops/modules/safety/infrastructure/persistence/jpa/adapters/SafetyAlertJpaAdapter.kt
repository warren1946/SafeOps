/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 * Permission granted for use, modification, and distribution with attribution.
 * No warranty provided.
 */

package com.zama.safeops.modules.safety.infrastructure.persistence.jpa.adapters

import com.zama.safeops.modules.safety.application.ports.SafetyAlertPort
import com.zama.safeops.modules.safety.domain.model.SafetyAlert
import com.zama.safeops.modules.safety.domain.valueobjects.SafetyAlertId
import com.zama.safeops.modules.safety.domain.valueobjects.SafetyEventId
import com.zama.safeops.modules.safety.infrastructure.persistence.jpa.entities.SafetyAlertJpaEntity
import com.zama.safeops.modules.safety.infrastructure.persistence.jpa.repositories.SpringDataSafetyAlertRepository
import org.springframework.stereotype.Component

@Component
class SafetyAlertJpaAdapter(
    private val repo: SpringDataSafetyAlertRepository
) : SafetyAlertPort {

    override fun create(alert: SafetyAlert): SafetyAlert =
        repo.save(alert.toEntity()).toDomain()

    override fun update(alert: SafetyAlert): SafetyAlert =
        repo.save(alert.toEntity()).toDomain()

    override fun findById(id: SafetyAlertId): SafetyAlert? =
        repo.findById(id.value).orElse(null)?.toDomain()
}

private fun SafetyAlert.toEntity() = SafetyAlertJpaEntity(
    id = id?.value,
    eventId = eventId.value,
    alertType = alertType,
    message = message,
    recipientId = recipientId,
    acknowledged = acknowledged,
    acknowledgedAt = acknowledgedAt,
    createdAt = createdAt
)

private fun SafetyAlertJpaEntity.toDomain() = SafetyAlert(
    id = id?.let { SafetyAlertId(it) },
    eventId = SafetyEventId(eventId),
    alertType = alertType,
    message = message,
    recipientId = recipientId,
    acknowledged = acknowledged,
    acknowledgedAt = acknowledgedAt,
    createdAt = createdAt
)