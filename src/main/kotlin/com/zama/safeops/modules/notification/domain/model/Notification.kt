/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 */

package com.zama.safeops.modules.notification.domain.model

import com.zama.safeops.modules.tenant.domain.valueobjects.TenantId
import java.time.Instant

/**
 * Domain model for notifications across all channels.
 */
data class Notification(
    val id: NotificationId? = null,
    val tenantId: TenantId,
    val recipient: Recipient,
    val channel: NotificationChannel,
    val type: NotificationType,
    val subject: String,
    val content: String,
    val templateId: String? = null,
    val templateData: Map<String, Any> = emptyMap(),
    val status: NotificationStatus = NotificationStatus.PENDING,
    val priority: NotificationPriority = NotificationPriority.NORMAL,
    val scheduledAt: Instant? = null,
    val sentAt: Instant? = null,
    val deliveredAt: Instant? = null,
    val errorMessage: String? = null,
    val retryCount: Int = 0,
    val createdAt: Instant = Instant.now()
) {
    fun markAsSent(): Notification = copy(
        status = NotificationStatus.SENT,
        sentAt = Instant.now()
    )

    fun markAsDelivered(): Notification = copy(
        status = NotificationStatus.DELIVERED,
        deliveredAt = Instant.now()
    )

    fun markAsFailed(error: String): Notification = copy(
        status = NotificationStatus.FAILED,
        errorMessage = error,
        retryCount = retryCount + 1
    )

    fun canRetry(maxRetries: Int): Boolean =
        status == NotificationStatus.FAILED && retryCount < maxRetries
}

@JvmInline
value class NotificationId(val value: Long)

data class Recipient(
    val userId: Long? = null,
    val email: String? = null,
    val phoneNumber: String? = null,
    val deviceToken: String? = null,
    val whatsAppId: String? = null
)

enum class NotificationChannel {
    EMAIL,
    SMS,
    PUSH,
    WHATSAPP,
    IN_APP
}

enum class NotificationType {
    // Safety-related
    SAFETY_ALERT_CRITICAL,
    SAFETY_ALERT_HIGH,
    SAFETY_EVENT_REPORTED,
    HAZARD_CREATED,
    HAZARD_RESOLVED,

    // Inspection-related
    INSPECTION_ASSIGNED,
    INSPECTION_DUE,
    INSPECTION_COMPLETED,
    INSPECTION_OVERDUE,
    REVIEW_REQUIRED,

    // User-related
    WELCOME,
    PASSWORD_RESET,
    ACCOUNT_LOCKED,
    ROLE_CHANGED,

    // System
    SYSTEM_MAINTENANCE,
    DATA_EXPORT_READY,
    REPORT_GENERATED
}

enum class NotificationStatus {
    PENDING,
    SCHEDULED,
    SENDING,
    SENT,
    DELIVERED,
    READ,
    FAILED,
    CANCELLED
}

enum class NotificationPriority {
    LOW,
    NORMAL,
    HIGH,
    CRITICAL
}
