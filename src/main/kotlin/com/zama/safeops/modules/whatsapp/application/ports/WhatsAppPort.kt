/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 * Permission granted for use, modification, and distribution with attribution.
 * No warranty provided.
 */

package com.zama.safeops.modules.whatsapp.application.ports

import com.zama.safeops.modules.whatsapp.domain.model.WhatsAppResponse

/**
 * Port for WhatsApp Business API integration.
 */
interface WhatsAppPort {

    /**
     * Send a simple text message.
     */
    fun sendMessage(to: String, message: String)

    /**
     * Send a structured response (with buttons, media, etc.).
     */
    fun sendResponse(response: WhatsAppResponse)

    /**
     * Send a template message.
     */
    fun sendTemplate(to: String, templateName: String, languageCode: String, parameters: Map<String, String>)

    /**
     * Mark a message as read.
     */
    fun markAsRead(messageId: String)
}
