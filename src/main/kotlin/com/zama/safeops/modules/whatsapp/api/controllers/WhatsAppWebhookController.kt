/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 * Permission granted for use, modification, and distribution with attribution.
 * No warranty provided.
 */

package com.zama.safeops.modules.whatsapp.api.controllers

import com.zama.safeops.modules.whatsapp.application.services.InspectionWorkflowEngine
import com.zama.safeops.modules.whatsapp.application.services.WhatsAppCommandParser
import com.zama.safeops.modules.whatsapp.domain.model.WhatsAppMessage
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/whatsapp/webhook")
class WhatsAppWebhookController(
    private val parser: WhatsAppCommandParser,
    private val engine: InspectionWorkflowEngine
) {

    data class IncomingWhatsAppPayload(
        val messageId: String,
        val from: String,
        val text: String? = null,
        val mediaUrl: String? = null
    )

    @PostMapping
    fun receive(@RequestBody payload: IncomingWhatsAppPayload) {
        val message = WhatsAppMessage(
            messageId = payload.messageId,
            from = payload.from,
            text = payload.text,
            mediaUrl = payload.mediaUrl
        )
        // Process through state machine-based engine
        engine.handle(message)
    }
}