/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 * Permission granted for use, modification, and distribution with attribution.
 * No warranty provided.
 */

package com.zama.safeops.modules.safety.application.services

import com.zama.safeops.modules.auth.application.ports.UserPort
import com.zama.safeops.modules.auth.domain.valueobjects.UserId
import com.zama.safeops.modules.safety.application.exceptions.SafetyAlertNotFoundException
import com.zama.safeops.modules.safety.application.exceptions.SafetyInvalidInputException
import com.zama.safeops.modules.safety.application.ports.SafetyAlertPort
import com.zama.safeops.modules.safety.domain.model.SafetyAlert
import com.zama.safeops.modules.safety.domain.valueobjects.SafetyAlertId
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant

@Service
class SafetyAlertService(
    private val alertPort: SafetyAlertPort,
    private val userPort: UserPort
) {

    @Transactional(readOnly = true)
    fun get(id: Long): SafetyAlert =
        alertPort.findById(SafetyAlertId(id)) ?: throw SafetyAlertNotFoundException(id)

    @Transactional
    fun acknowledge(id: Long): SafetyAlert {
        val alert = get(id)

        val updated = alert.copy(
            acknowledged = true,
            acknowledgedAt = Instant.now()
        )

        val saved = alertPort.update(updated)
        if (saved.id == null) error("SafetyAlert ID must not be null after update()")
        return saved
    }

    fun validateRecipient(recipientId: Long) {
        if (!userPort.existsById(UserId(recipientId))) {
            throw SafetyInvalidInputException("Recipient with ID $recipientId does not exist")
        }
    }
}