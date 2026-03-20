/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 */

package com.zama.safeops.modules.notification.infrastructure.adapters

import com.zama.safeops.modules.notification.application.services.NotificationTemplate
import com.zama.safeops.modules.notification.application.services.NotificationTemplateService
import com.zama.safeops.modules.notification.application.services.RenderedContent
import com.zama.safeops.modules.notification.domain.model.NotificationChannel
import com.zama.safeops.modules.notification.domain.model.NotificationType
import com.zama.safeops.modules.tenant.domain.valueobjects.TenantId
import org.springframework.stereotype.Service

/**
 * Stub implementation of NotificationTemplateService.
 * Returns default templates for common notification types.
 */
@Service
class StubNotificationTemplateService : NotificationTemplateService {

    private val defaultTemplates = mapOf(
        "WELCOME_EMAIL" to NotificationTemplate(
            id = "WELCOME_EMAIL",
            type = NotificationType.WELCOME,
            subjectTemplate = "Welcome to SafeOps, {{name}}!",
            bodyTemplate = "Hi {{name}},\n\nWelcome to SafeOps. Your account has been created successfully.",
            channel = NotificationChannel.EMAIL
        ),
        "PASSWORD_RESET" to NotificationTemplate(
            id = "PASSWORD_RESET",
            type = NotificationType.PASSWORD_RESET,
            subjectTemplate = "Password Reset Request",
            bodyTemplate = "Click the link to reset your password: {{resetLink}}",
            channel = NotificationChannel.EMAIL
        ),
        "SAFETY_ALERT" to NotificationTemplate(
            id = "SAFETY_ALERT",
            type = NotificationType.SAFETY_ALERT_CRITICAL,
            subjectTemplate = "CRITICAL SAFETY ALERT: {{alertTitle}}",
            bodyTemplate = "A critical safety alert has been triggered: {{alertDescription}}. Please take immediate action.",
            channel = NotificationChannel.IN_APP
        ),
        "INSPECTION_ASSIGNED" to NotificationTemplate(
            id = "INSPECTION_ASSIGNED",
            type = NotificationType.INSPECTION_ASSIGNED,
            subjectTemplate = "New Inspection Assigned",
            bodyTemplate = "You have been assigned a new inspection: {{inspectionName}}. Due date: {{dueDate}}",
            channel = NotificationChannel.IN_APP
        )
    )

    override fun getTemplate(tenantId: TenantId, templateId: String, channel: NotificationChannel): NotificationTemplate {
        return defaultTemplates[templateId]
            ?: NotificationTemplate(
                id = templateId,
                type = NotificationType.SYSTEM_MAINTENANCE,
                subjectTemplate = "Notification: {{subject}}",
                bodyTemplate = "{{message}}",
                channel = channel
            )
    }

    override fun renderTemplate(template: NotificationTemplate, data: Map<String, Any>): RenderedContent {
        var subject = template.subjectTemplate
        var body = template.bodyTemplate

        data.forEach { (key, value) ->
            subject = subject.replace("{{$key}}", value.toString())
            body = body.replace("{{$key}}", value.toString())
        }

        // Clean up any remaining template variables
        val placeholderRegex = """\{\{[^}]+}}""".toRegex()
        subject = subject.replace(placeholderRegex, "")
        body = body.replace(placeholderRegex, "")

        return RenderedContent(subject = subject, body = body)
    }
}
