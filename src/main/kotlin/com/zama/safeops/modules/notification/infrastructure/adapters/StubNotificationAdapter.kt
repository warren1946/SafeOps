/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 */

package com.zama.safeops.modules.notification.infrastructure.adapters

import com.zama.safeops.modules.notification.application.ports.*
import com.zama.safeops.modules.notification.domain.model.Notification
import com.zama.safeops.modules.notification.domain.model.NotificationId
import com.zama.safeops.modules.notification.domain.model.NotificationStatus
import com.zama.safeops.modules.tenant.domain.valueobjects.TenantId
import org.springframework.stereotype.Component

/**
 * Stub implementation of NotificationPort.
 * Returns empty results until a real implementation is configured.
 */
@Component
class StubNotificationAdapter : NotificationPort {

    override fun save(notification: Notification): Notification = notification

    override fun findById(id: NotificationId): Notification? = null

    override fun findPending(tenantId: TenantId, limit: Int): List<Notification> = emptyList()

    override fun findByStatus(tenantId: TenantId, status: NotificationStatus): List<Notification> = emptyList()

    override fun findByRecipient(userId: Long, limit: Int): List<Notification> = emptyList()

    override fun findUnreadByRecipient(userId: Long): List<Notification> = emptyList()

    override fun markAsRead(notificationId: NotificationId): Notification {
        throw NotImplementedError("Stub implementation")
    }
}

/**
 * Stub implementation of EmailPort.
 */
@Component
class StubEmailAdapter : EmailPort {
    override fun sendEmail(
        to: String,
        subject: String,
        htmlBody: String,
        textBody: String?,
        attachments: List<EmailAttachment>
    ): SendResult = SendResult.Success("stub-email-id")

    override fun sendTemplateEmail(
        to: String,
        templateId: String,
        templateData: Map<String, Any>,
        attachments: List<EmailAttachment>
    ): SendResult = SendResult.Success("stub-template-email-id")
}

/**
 * Stub implementation of SmsPort.
 */
@Component
class StubSmsAdapter : SmsPort {
    override fun sendSms(phoneNumber: String, message: String): SendResult = SendResult.Success("stub-sms-id")
}

/**
 * Stub implementation of PushNotificationPort.
 */
@Component
class StubPushNotificationAdapter : PushNotificationPort {
    override fun sendPush(
        deviceToken: String,
        title: String,
        body: String,
        data: Map<String, String>
    ): SendResult = SendResult.Success("stub-push-id")

    override fun sendMulticast(
        deviceTokens: List<String>,
        title: String,
        body: String,
        data: Map<String, String>
    ): SendResult = SendResult.Success("stub-multicast-id")
}
