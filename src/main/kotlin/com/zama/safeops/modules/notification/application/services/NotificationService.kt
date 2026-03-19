/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 */

package com.zama.safeops.modules.notification.application.services

import com.zama.safeops.modules.notification.application.ports.NotificationPort
import com.zama.safeops.modules.notification.application.ports.NotificationSender
import com.zama.safeops.modules.notification.application.ports.SendResult
import com.zama.safeops.modules.notification.domain.model.*
import com.zama.safeops.modules.tenant.domain.valueobjects.TenantId
import org.springframework.scheduling.annotation.Async
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * Service for managing notification lifecycle across all channels.
 */
@Service
@Transactional
class NotificationService(
    private val notificationPort: NotificationPort,
    private val senders: List<NotificationSender>,
    private val templateService: NotificationTemplateService
) {

    /**
     * Create and immediately send a notification.
     */
    fun sendNotification(notification: Notification): Notification {
        val saved = notificationPort.save(notification)
        return processNotification(saved)
    }

    /**
     * Schedule a notification for later delivery.
     */
    fun scheduleNotification(notification: Notification): Notification {
        require(notification.scheduledAt != null) { "Scheduled notification must have scheduledAt time" }
        return notificationPort.save(notification.copy(status = NotificationStatus.SCHEDULED))
    }

    /**
     * Send notification using a template.
     */
    fun sendTemplatedNotification(
        tenantId: TenantId,
        recipient: Recipient,
        channel: NotificationChannel,
        templateId: String,
        templateData: Map<String, Any>,
        priority: NotificationPriority = NotificationPriority.NORMAL
    ): Notification {
        val template = templateService.getTemplate(tenantId, templateId, channel)
        val content = templateService.renderTemplate(template, templateData)

        val notification = Notification(
            tenantId = tenantId,
            recipient = recipient,
            channel = channel,
            type = template.type,
            subject = content.subject,
            content = content.body,
            templateId = templateId,
            templateData = templateData,
            priority = priority
        )

        return sendNotification(notification)
    }

    /**
     * Get notifications for a user.
     */
    fun getUserNotifications(userId: Long, limit: Int = 50): List<Notification> {
        return notificationPort.findByRecipient(userId, limit)
    }

    /**
     * Get unread notifications for a user.
     */
    fun getUnreadNotifications(userId: Long): List<Notification> {
        return notificationPort.findUnreadByRecipient(userId)
    }

    /**
     * Mark a notification as read.
     */
    fun markAsRead(notificationId: NotificationId): Notification {
        return notificationPort.markAsRead(notificationId)
    }

    /**
     * Process pending notifications (called by scheduler).
     */
    @Scheduled(fixedDelay = 30000) // Every 30 seconds
    @Transactional
    fun processPendingNotifications() {
        // This would process notifications for all tenants
        // In a real implementation, you'd want to shard by tenant
        val pendingNotifications = mutableListOf<Notification>()

        pendingNotifications.forEach { notification ->
            try {
                processNotification(notification)
            } catch (e: Exception) {
                // Log error but continue processing other notifications
                println("Failed to process notification ${notification.id}: ${e.message}")
            }
        }
    }

    /**
     * Process scheduled notifications that are due.
     */
    @Scheduled(fixedDelay = 60000) // Every minute
    @Transactional
    fun processScheduledNotifications() {
        // Find scheduled notifications that are now due
        // and process them
    }

    @Async
    protected fun processNotification(notification: Notification): Notification {
        val sender = senders.find { it.channel == notification.channel }
            ?: throw IllegalStateException("No sender configured for channel ${notification.channel}")

        val result = sender.send(notification)

        val updatedNotification = when (result) {
            is SendResult.Success -> notification.markAsSent()
            is SendResult.Failure -> notification.markAsFailed(result.error)
        }

        return notificationPort.save(updatedNotification)
    }

    /**
     * Send bulk notifications (e.g., safety alerts to all affected users).
     */
    fun sendBulkNotifications(
        tenantId: TenantId,
        recipients: List<Recipient>,
        channel: NotificationChannel,
        type: NotificationType,
        subject: String,
        content: String,
        priority: NotificationPriority = NotificationPriority.HIGH
    ): List<Notification> {
        return recipients.map { recipient ->
            val notification = Notification(
                tenantId = tenantId,
                recipient = recipient,
                channel = channel,
                type = type,
                subject = subject,
                content = content,
                priority = priority
            )
            sendNotification(notification)
        }
    }
}

/**
 * Service for managing notification templates.
 */
interface NotificationTemplateService {
    fun getTemplate(tenantId: TenantId, templateId: String, channel: NotificationChannel): NotificationTemplate
    fun renderTemplate(template: NotificationTemplate, data: Map<String, Any>): RenderedContent
}

data class NotificationTemplate(
    val id: String,
    val type: NotificationType,
    val subjectTemplate: String,
    val bodyTemplate: String,
    val channel: NotificationChannel
)

data class RenderedContent(
    val subject: String,
    val body: String
)
