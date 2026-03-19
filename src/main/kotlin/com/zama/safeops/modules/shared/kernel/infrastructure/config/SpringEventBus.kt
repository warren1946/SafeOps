/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 */

package com.zama.safeops.modules.shared.kernel.infrastructure.config

import com.zama.safeops.modules.shared.kernel.application.ports.EventBus
import com.zama.safeops.modules.shared.kernel.domain.events.DomainEvent
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import java.util.concurrent.ConcurrentHashMap

/**
 * Spring-based implementation of the event bus.
 * Uses Spring's ApplicationEventPublisher for loose coupling.
 */
@Component
class SpringEventBus(
    private val applicationEventPublisher: ApplicationEventPublisher
) : EventBus {

    private val handlers = ConcurrentHashMap<Class<*>, MutableList<(Any) -> Unit>>()

    override fun publish(event: DomainEvent) {
        applicationEventPublisher.publishEvent(event)
    }

    override fun publish(events: List<DomainEvent>) {
        events.forEach { publish(it) }
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : DomainEvent> subscribe(eventType: Class<T>, handler: (T) -> Unit) {
        handlers.computeIfAbsent(eventType) { mutableListOf() }
            .add(handler as (Any) -> Unit)
    }

    /**
     * Listens to all domain events and dispatches to registered handlers.
     */
    @EventListener
    fun onDomainEvent(event: DomainEvent) {
        handlers[event::class.java]?.forEach { handler ->
            try {
                handler(event)
            } catch (e: Exception) {
                // Log error but don't prevent other handlers from executing
                println("Error handling event ${event.eventType}: ${e.message}")
            }
        }
    }
}

/**
 * Wrapper class for Spring application events.
 */
data class SafeOpsApplicationEvent(val domainEvent: DomainEvent)
