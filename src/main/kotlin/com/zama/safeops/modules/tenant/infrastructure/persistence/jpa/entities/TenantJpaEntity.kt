/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 */

package com.zama.safeops.modules.tenant.infrastructure.persistence.jpa.entities

import jakarta.persistence.*
import java.time.Instant

@Entity
@Table(name = "tenants")
class TenantJpaEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false, unique = true)
    val slug: String,

    @Column(nullable = false)
    val name: String,

    @Column(nullable = false)
    val status: String = "PENDING",

    @Column(name = "subscription_plan", nullable = false)
    val subscriptionPlan: String = "BASIC",

    @Column(name = "created_at", nullable = false)
    val createdAt: Instant = Instant.now(),

    @Column(name = "updated_at", nullable = false)
    val updatedAt: Instant = Instant.now(),

    @Column(name = "activated_at")
    val activatedAt: Instant?,

    @Column(name = "default_language", nullable = false)
    val defaultLanguage: String = "en",

    @Column(nullable = false)
    val timezone: String = "UTC",

    @Column(name = "date_format", nullable = false)
    val dateFormat: String = "yyyy-MM-dd",

    @Column(name = "time_format", nullable = false)
    val timeFormat: String = "HH:mm",

    // Feature flags
    @Column(name = "feature_whatsapp", nullable = false)
    val featureWhatsApp: Boolean = true,

    @Column(name = "feature_advanced_reporting", nullable = false)
    val featureAdvancedReporting: Boolean = false,

    @Column(name = "feature_custom_branding", nullable = false)
    val featureCustomBranding: Boolean = false,

    @Column(name = "feature_api_access", nullable = false)
    val featureApiAccess: Boolean = false,

    @Column(name = "feature_multi_mine", nullable = false)
    val featureMultiMine: Boolean = true,

    @Column(name = "feature_offline_mode", nullable = false)
    val featureOfflineMode: Boolean = false,

    @Column(name = "feature_photo_evidence", nullable = false)
    val featurePhotoEvidence: Boolean = true,

    @Column(name = "feature_gps_tracking", nullable = false)
    val featureGpsTracking: Boolean = true,

    // WhatsApp Configuration
    @Column(name = "whatsapp_phone_id")
    val whatsappPhoneId: String?,

    @Column(name = "whatsapp_account_id")
    val whatsappAccountId: String?,

    @Column(name = "whatsapp_access_token")
    val whatsappAccessToken: String?,

    @Column(name = "whatsapp_webhook_secret")
    val whatsappWebhookSecret: String?,

    @Column(name = "whatsapp_welcome_msg")
    val whatsappWelcomeMsg: String = "Welcome to SafeOps! Send START INSPECTION to begin.",

    // Notification Settings
    @Column(name = "email_enabled", nullable = false)
    val emailEnabled: Boolean = true,

    @Column(name = "sms_enabled", nullable = false)
    val smsEnabled: Boolean = false,

    @Column(name = "push_enabled", nullable = false)
    val pushEnabled: Boolean = true,

    @Column(name = "whatsapp_enabled", nullable = false)
    val whatsappEnabled: Boolean = true,

    // Branding
    @Column(name = "primary_color")
    val primaryColor: String = "#1e3a5f",

    @Column(name = "secondary_color")
    val secondaryColor: String = "#e63946",

    @Column(name = "logo_url")
    val logoUrl: String?,

    @Column(name = "favicon_url")
    val faviconUrl: String?,

    @Column(name = "app_name")
    val appName: String = "SafeOps",

    @Column(name = "support_email")
    val supportEmail: String?,

    @Column(name = "support_phone")
    val supportPhone: String?
)
