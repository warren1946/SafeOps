/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 * Permission granted for use, modification, and distribution with attribution.
 * No warranty provided.
 */

package com.zama.safeops.modules.whatsapp.application.ports

interface OfficerPresencePort {
    fun checkIn(officerPhone: String, locationCode: String)
    fun checkOut(officerPhone: String)
}