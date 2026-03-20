/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 */

package com.zama.safeops.modules.tenant.infrastructure.persistence

import com.zama.safeops.modules.tenant.domain.model.*
import com.zama.safeops.modules.tenant.domain.valueobjects.*
import jakarta.persistence.*
import java.time.Instant

@Entity
@Table(name = "tenants")
class TenantJpaEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false, unique = true, length = 50)
    val slug: String,

    @Column(nullable = false, length = 200)
    var name: String,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    var status: TenantStatus = TenantStatus.PENDING,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    var subscriptionPlan: SubscriptionPlan = SubscriptionPlan.BASIC,

    @Column(nullable = false)
    var createdAt: Instant = Instant.now(),

    @Column(nullable = false)
    var updatedAt: Instant = Instant.now(),

    var activatedAt: Instant? = null,

    // Configuration
    @Column(nullable = false, length = 10)
    var defaultLanguage: String = "en",

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "tenant_languages", joinColumns = [JoinColumn(name = "tenant_id", nullable = false)])
    @Column(name = "language_code", nullable = false)
    var supportedLanguages: MutableSet<String> = mutableSetOf("en"),

    @Column(nullable = false, length = 50)
    var timezone: String = "UTC",

    @Column(nullable = false, length = 20)
    var dateFormat: String = "yyyy-MM-dd",

    @Column(nullable = false, length = 20)
    var timeFormat: String = "HH:mm",

    // Feature flags
    @Column(nullable = false)
    var featureWhatsApp: Boolean = true,

    @Column(nullable = false)
    var featureAdvancedReporting: Boolean = false,

    @Column(nullable = false)
    var featureCustomBranding: Boolean = false,

    @Column(nullable = false)
    var featureApiAccess: Boolean = false,

    @Column(nullable = false)
    var featureMultiMine: Boolean = true,

    @Column(nullable = false)
    var featureOfflineMode: Boolean = false,

    @Column(nullable = false)
    var featurePhotoEvidence: Boolean = true,

    @Column(nullable = false)
    var featureGpsTracking: Boolean = true,

    // WhatsApp Configuration
    @Column(name = "whatsapp_phone_id", length = 100)
    var whatsAppPhoneNumberId: String? = null,

    @Column(name = "whatsapp_account_id", length = 100)
    var whatsAppBusinessAccountId: String? = null,

    @Column(name = "whatsapp_access_token", length = 500)
    var whatsAppAccessToken: String? = null,

    @Column(name = "whatsapp_webhook_secret", length = 200)
    var whatsAppWebhookSecret: String? = null,

    @Column(name = "whatsapp_welcome_msg", length = 500)
    var whatsAppWelcomeMessage: String = "Welcome to SafeOps! Send START INSPECTION to begin.",

    // Notification Settings
    @Column(nullable = false)
    var emailEnabled: Boolean = true,

    @Column(nullable = false)
    var smsEnabled: Boolean = false,

    @Column(nullable = false)
    var pushEnabled: Boolean = true,

    @Column(nullable = false)
    var whatsAppEnabled: Boolean = true,

    // Branding
    @Column(length = 10)
    var primaryColor: String = "#1e3a5f",

    @Column(length = 10)
    var secondaryColor: String = "#e63946",

    @Column(length = 500)
    var logoUrl: String? = null,

    @Column(length = 500)
    var faviconUrl: String? = null,

    @Column(name = "app_name", length = 100)
    var appName: String = "SafeOps",

    @Column(length = 200)
    var supportEmail: String? = null,

    @Column(length = 50)
    var supportPhone: String? = null
) {
    fun toDomain(): Tenant = Tenant(
        id = id?.let { TenantId(it) },
        slug = TenantSlug(slug),
        name = TenantName(name),
        status = status,
        subscriptionPlan = subscriptionPlan,
        createdAt = createdAt,
        updatedAt = updatedAt,
        activatedAt = activatedAt,
        configuration = TenantConfiguration(
            defaultLanguage = Language.fromCode(defaultLanguage),
            supportedLanguages = supportedLanguages.map { Language.fromCode(it) }.toSet(),
            timezone = timezone,
            dateFormat = dateFormat,
            timeFormat = timeFormat,
            features = FeatureFlags(
                whatsAppIntegration = featureWhatsApp,
                advancedReporting = featureAdvancedReporting,
                customBranding = featureCustomBranding,
                apiAccess = featureApiAccess,
                multiMineSupport = featureMultiMine,
                offlineMode = featureOfflineMode,
                photoEvidence = featurePhotoEvidence,
                gpsTracking = featureGpsTracking
            ),
            whatsAppConfig = WhatsAppConfiguration(
                phoneNumberId = whatsAppPhoneNumberId,
                businessAccountId = whatsAppBusinessAccountId,
                accessToken = whatsAppAccessToken,
                webhookSecret = whatsAppWebhookSecret,
                welcomeMessage = whatsAppWelcomeMessage
            ),
            notificationSettings = NotificationSettings(
                emailEnabled = emailEnabled,
                smsEnabled = smsEnabled,
                pushEnabled = pushEnabled,
                whatsAppEnabled = whatsAppEnabled
            )
        ),
        branding = TenantBranding(
            primaryColor = primaryColor,
            secondaryColor = secondaryColor,
            logoUrl = logoUrl,
            faviconUrl = faviconUrl,
            appName = appName,
            supportEmail = supportEmail,
            supportPhone = supportPhone
        )
    )

    companion object {
        fun fromDomain(tenant: Tenant): TenantJpaEntity = TenantJpaEntity(
            id = tenant.id?.value,
            slug = tenant.slug.value,
            name = tenant.name.value,
            status = tenant.status,
            subscriptionPlan = tenant.subscriptionPlan,
            createdAt = tenant.createdAt,
            updatedAt = tenant.updatedAt,
            activatedAt = tenant.activatedAt,
            defaultLanguage = tenant.configuration.defaultLanguage.code,
            supportedLanguages = tenant.configuration.supportedLanguages.map { it.code }.toMutableSet(),
            timezone = tenant.configuration.timezone,
            dateFormat = tenant.configuration.dateFormat,
            timeFormat = tenant.configuration.timeFormat,
            featureWhatsApp = tenant.configuration.features.whatsAppIntegration,
            featureAdvancedReporting = tenant.configuration.features.advancedReporting,
            featureCustomBranding = tenant.configuration.features.customBranding,
            featureApiAccess = tenant.configuration.features.apiAccess,
            featureMultiMine = tenant.configuration.features.multiMineSupport,
            featureOfflineMode = tenant.configuration.features.offlineMode,
            featurePhotoEvidence = tenant.configuration.features.photoEvidence,
            featureGpsTracking = tenant.configuration.features.gpsTracking,
            whatsAppPhoneNumberId = tenant.configuration.whatsAppConfig?.phoneNumberId,
            whatsAppBusinessAccountId = tenant.configuration.whatsAppConfig?.businessAccountId,
            whatsAppAccessToken = tenant.configuration.whatsAppConfig?.accessToken,
            whatsAppWebhookSecret = tenant.configuration.whatsAppConfig?.webhookSecret,
            whatsAppWelcomeMessage = tenant.configuration.whatsAppConfig?.welcomeMessage
                ?: "Welcome to SafeOps! Send START INSPECTION to begin.",
            emailEnabled = tenant.configuration.notificationSettings.emailEnabled,
            smsEnabled = tenant.configuration.notificationSettings.smsEnabled,
            pushEnabled = tenant.configuration.notificationSettings.pushEnabled,
            whatsAppEnabled = tenant.configuration.notificationSettings.whatsAppEnabled,
            primaryColor = tenant.branding.primaryColor,
            secondaryColor = tenant.branding.secondaryColor,
            logoUrl = tenant.branding.logoUrl,
            faviconUrl = tenant.branding.faviconUrl,
            appName = tenant.branding.appName,
            supportEmail = tenant.branding.supportEmail,
            supportPhone = tenant.branding.supportPhone
        )
    }
}
