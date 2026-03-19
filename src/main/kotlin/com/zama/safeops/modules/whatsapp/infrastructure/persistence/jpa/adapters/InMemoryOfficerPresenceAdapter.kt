/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 * Permission granted for use, modification, and distribution with attribution.
 * No warranty provided.
 */

package com.zama.safeops.modules.whatsapp.infrastructure.persistence.jpa.adapters

import com.zama.safeops.modules.whatsapp.application.ports.OfficerPresencePort
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.util.concurrent.ConcurrentHashMap

@Component
class InMemoryOfficerPresenceAdapter : OfficerPresencePort {

    private val log = LoggerFactory.getLogger(javaClass)
    private val presence = ConcurrentHashMap<String, String>() // phone -> locationCode

    override fun checkIn(officerPhone: String, locationCode: String) {
        presence[officerPhone] = locationCode
        log.info("Officer {} checked in at {}", officerPhone, locationCode)
    }

    override fun checkOut(officerPhone: String) {
        presence.remove(officerPhone)
        log.info("Officer {} checked out", officerPhone)
    }
}