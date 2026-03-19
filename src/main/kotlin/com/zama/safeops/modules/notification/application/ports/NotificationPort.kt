/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 */

package com.zama.safeops.modules.notification.application.ports

import com.zama.safeops.modules.notification.domain.model.Notification
import com.zama.safeops.modules.notification.domain.model.NotificationChannel
import com.zama.safeops.modules.notification.domain.model.NotificationId
import com.zama.safeops.modules.notification.domain.model.NotificationStatus
import com.zama.safeops.modules.tenant.domain.valueobjects.TenantId

/**
 * Port for notification persistence.
 */
interface NotificationPort {
    fun save(notification: Notification): Notification
    fun findById(id: NotificationId): Notification?
    fun findPending(tenantId: TenantId, limit: Int = 100): List<Notification>
    fun findByStatus(tenantId: TenantId, status: NotificationStatus): List<Notification>
    fun findByRecipient(userId: Long, limit: Int = 50): List<Notification>
    fun findUnreadByRecipient(userId: Long): List<Notification>
    fun markAsRead(notificationId: NotificationId): Notification
}

/**
 * Port for sending notifications through various channels.
 */
interface NotificationSender {
    val channel: NotificationChannel
    fun send(notification: Notification): SendResult
}

sealed class SendResult {
    data class Success(val externalId: String? = null) : SendResult()
    data class Failure(val error: String, val retryable: Boolean = true) : SendResult()
}

/**
 * Port for email-specific operations.
 */
interface EmailPort {
    fun sendEmail(
        to: String,
        subject: String,
        htmlBody: String,
        textBody: String? = null,
        attachments: List<EmailAttachment> = emptyList()
    ): SendResult

    fun sendTemplateEmail(
        to: String,
        templateId: String,
        templateData: Map<String, Any>,
        attachments: List<EmailAttachment> = emptyList()
    ): SendResult
}

data class EmailAttachment(
    val filename: String,
    val contentType: String,
    val content: ByteArray
)

/**
 * Port for SMS operations.
 */
interface SmsPort {
    fun sendSms(phoneNumber: String, message: String): SendResult
}

/**
 * Port for push notification operations.
 */
interface PushNotificationPort {
    fun sendPush(deviceToken: String, title: String, body: String, data: Map<String, String> = emptyMap()): SendResult
    fun sendMulticast(deviceTokens: List<String>, title: String, body: String, data: Map<String, String> = emptyMap()): SendResult
}
