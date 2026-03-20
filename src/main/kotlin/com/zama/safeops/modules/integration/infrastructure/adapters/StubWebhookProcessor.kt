/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 */

package com.zama.safeops.modules.integration.infrastructure.adapters

import com.zama.safeops.modules.integration.application.services.WebhookProcessor
import com.zama.safeops.modules.integration.domain.model.WebhookEvent
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service

/**
 * Stub implementation of WebhookProcessor.
 * Logs webhook events for debugging without actual processing.
 */
@Service
class StubWebhookProcessor : WebhookProcessor {

    @Async
    override fun process(event: WebhookEvent) {
        // Stub implementation - just log the event
        println("[WebhookProcessor] Processing webhook event: ${event.id}")
        println("  Integration: ${event.integrationId}")
        println("  Tenant: ${event.tenantId.value}")
        println("  Event Type: ${event.eventType}")
        println("  Payload: ${event.payload.take(200)}...")

        // Simulate processing delay
        Thread.sleep(100)

        println("[WebhookProcessor] Webhook event ${event.id} processed successfully")
    }
}
