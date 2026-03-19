/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 * Permission granted for use, modification, and distribution with attribution.
 * No warranty provided.
 */

package com.zama.safeops.modules.whatsapp.infrastructure.persistence.jpa.adapters

import com.zama.safeops.modules.whatsapp.application.ports.WhatsAppPort
import com.zama.safeops.modules.whatsapp.domain.model.WhatsAppResponse
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

/**
 * HTTP adapter for WhatsApp Business API.
 * Currently a stub - replace with actual Meta Cloud API or Twilio integration.
 */
@Component
class WhatsAppHttpAdapter : WhatsAppPort {

    private val log = LoggerFactory.getLogger(javaClass)

    override fun sendMessage(to: String, message: String) {
        // TODO: integrate with real WhatsApp provider (e.g. Twilio, Meta Cloud API)
        log.info("[WhatsApp] Sending message to {}: {}", to, message)
    }

    override fun sendResponse(response: WhatsAppResponse) {
        log.info("[WhatsApp] Sending response to {}: {}", response.to, response.text)

        if (response.buttons.isNotEmpty()) {
            log.info("[WhatsApp] With buttons: {}", response.buttons.joinToString { it.title })
        }

        if (response.mediaUrl != null) {
            log.info("[WhatsApp] With media: {}", response.mediaUrl)
        }

        // TODO: Implement actual API call to WhatsApp Business API
        // https://graph.facebook.com/v18.0/{phone-number-id}/messages
    }

    override fun sendTemplate(to: String, templateName: String, languageCode: String, parameters: Map<String, String>) {
        log.info("[WhatsApp] Sending template '{}' to {} (language: {})", templateName, to, languageCode)
        // TODO: Implement template message sending
    }

    override fun markAsRead(messageId: String) {
        log.info("[WhatsApp] Marking message {} as read", messageId)
        // TODO: Implement read receipt
    }
}
