/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 */

package com.zama.safeops.modules.tenant.domain.model

import com.zama.safeops.modules.tenant.domain.valueobjects.*
import java.time.Instant

/**
 * Tenant aggregate root representing a white-label client.
 * Each mine/organization gets their own tenant with complete data isolation.
 */
data class Tenant(
    val id: TenantId? = null,
    val slug: TenantSlug,
    val name: TenantName,
    val status: TenantStatus = TenantStatus.PENDING,
    val subscriptionPlan: SubscriptionPlan = SubscriptionPlan.BASIC,
    val configuration: TenantConfiguration = TenantConfiguration(),
    val branding: TenantBranding = TenantBranding(),
    val createdAt: Instant = Instant.now(),
    val updatedAt: Instant = Instant.now(),
    val activatedAt: Instant? = null
) {

    fun activate(): Tenant = copy(
        status = TenantStatus.ACTIVE,
        activatedAt = Instant.now(),
        updatedAt = Instant.now()
    )

    fun suspend(): Tenant = copy(
        status = TenantStatus.SUSPENDED,
        updatedAt = Instant.now()
    )

    fun updateConfiguration(newConfig: TenantConfiguration): Tenant = copy(
        configuration = newConfig,
        updatedAt = Instant.now()
    )

    fun updateBranding(newBranding: TenantBranding): Tenant = copy(
        branding = newBranding,
        updatedAt = Instant.now()
    )

    fun isActive(): Boolean = status == TenantStatus.ACTIVE
}

/**
 * Configuration settings for a tenant.
 * These settings allow customization per white-label client.
 */
data class TenantConfiguration(
    val defaultLanguage: Language = Language.ENGLISH,
    val supportedLanguages: Set<Language> = setOf(Language.ENGLISH),
    val timezone: String = "UTC",
    val dateFormat: String = "yyyy-MM-dd",
    val timeFormat: String = "HH:mm",
    val features: FeatureFlags = FeatureFlags(),
    val whatsAppConfig: WhatsAppConfiguration? = null,
    val notificationSettings: NotificationSettings = NotificationSettings(),
    val inspectionSettings: InspectionSettings = InspectionSettings()
)

/**
 * Supported languages for the platform.
 */
enum class Language(val code: String, val displayName: String) {
    ENGLISH("en", "English"),
    PORTUGUESE("pt", "Português"),
    FRENCH("fr", "Français"),
    SWAHILI("sw", "Kiswahili"),
    AFRIKAANS("af", "Afrikaans"),
    ZULU("zu", "isiZulu");

    companion object {
        fun fromCode(code: String): Language =
            entries.find { it.code.equals(code, ignoreCase = true) }
                ?: ENGLISH
    }
}

/**
 * Feature flags to enable/disable functionality per tenant.
 */
data class FeatureFlags(
    val whatsAppIntegration: Boolean = true,
    val advancedReporting: Boolean = false,
    val customBranding: Boolean = false,
    val apiAccess: Boolean = false,
    val multiMineSupport: Boolean = true,
    val offlineMode: Boolean = false,
    val photoEvidence: Boolean = true,
    val gpsTracking: Boolean = true
)

/**
 * WhatsApp Business API configuration per tenant.
 */
data class WhatsAppConfiguration(
    val phoneNumberId: String? = null,
    val businessAccountId: String? = null,
    val accessToken: String? = null,
    val webhookSecret: String? = null,
    val welcomeMessage: String = "Welcome to SafeOps! Send START INSPECTION to begin.",
    val enabledCommands: Set<String> = setOf("START", "REPORT", "STATUS", "HELP", "EMERGENCY")
)

/**
 * Notification settings per tenant.
 */
data class NotificationSettings(
    val emailEnabled: Boolean = true,
    val smsEnabled: Boolean = false,
    val pushEnabled: Boolean = true,
    val whatsAppEnabled: Boolean = true,
    val alertEmailRecipients: List<String> = emptyList(),
    val criticalAlertWebhook: String? = null
)

/**
 * Inspection-specific settings per tenant.
 */
data class InspectionSettings(
    val requirePhotoEvidence: Boolean = false,
    val autoAssignReviewer: Boolean = true,
    val defaultReviewTimeoutHours: Int = 48,
    val allowOfflineInspections: Boolean = true,
    val minimumInspectionDurationMinutes: Int = 5
)

/**
 * Branding configuration for white-label support.
 */
data class TenantBranding(
    val primaryColor: String = "#1e3a5f",
    val secondaryColor: String = "#e63946",
    val logoUrl: String? = null,
    val faviconUrl: String? = null,
    val customCss: String? = null,
    val appName: String = "SafeOps",
    val supportEmail: String? = null,
    val supportPhone: String? = null
)
