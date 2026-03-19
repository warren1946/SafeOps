/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 */

package com.zama.safeops.modules.shared.kernel.application.ports

import com.zama.safeops.modules.shared.kernel.domain.events.DomainEvent

/**
 * Port for publishing domain events.
 * Implementations can use various messaging mechanisms (in-memory, message bus, etc.)
 */
interface EventPublisher {

    /**
     * Publish a single domain event.
     */
    fun publish(event: DomainEvent)

    /**
     * Publish multiple domain events.
     */
    fun publish(events: List<DomainEvent>)
}

/**
 * Port for subscribing to domain events.
 */
interface EventSubscriber {

    /**
     * Subscribe to events of a specific type.
     */
    fun <T : DomainEvent> subscribe(eventType: Class<T>, handler: (T) -> Unit)
}

/**
 * Combined event bus interface.
 */
interface EventBus : EventPublisher, EventSubscriber
