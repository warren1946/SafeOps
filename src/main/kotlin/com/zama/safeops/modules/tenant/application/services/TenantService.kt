/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 */

package com.zama.safeops.modules.tenant.application.services

import com.zama.safeops.modules.shared.kernel.application.ports.EventPublisher
import com.zama.safeops.modules.shared.kernel.domain.events.TenantConfigurationUpdatedEvent
import com.zama.safeops.modules.shared.kernel.domain.events.TenantProvisionedEvent
import com.zama.safeops.modules.tenant.application.ports.TenantPort
import com.zama.safeops.modules.tenant.domain.model.*
import com.zama.safeops.modules.tenant.domain.valueobjects.SubscriptionPlan
import com.zama.safeops.modules.tenant.domain.valueobjects.TenantId
import com.zama.safeops.modules.tenant.domain.valueobjects.TenantName
import com.zama.safeops.modules.tenant.domain.valueobjects.TenantSlug
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.cache.annotation.Caching
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * Service for managing tenant lifecycle and configuration.
 *
 * Uses caching for:
 * - Tenant configuration (frequently accessed, rarely changed)
 * - Tenant metadata (basic tenant info)
 */
@Service
@Transactional
class TenantService(
    private val tenantPort: TenantPort,
    private val eventPublisher: EventPublisher
) {

    companion object {
        const val CACHE_TENANT_CONFIG = "tenant-config"
        const val CACHE_TENANT_METADATA = "tenant-metadata"
    }

    fun provisionTenant(
        slug: String,
        name: String,
        adminEmail: String,
        plan: SubscriptionPlan = SubscriptionPlan.BASIC,
        initialConfig: TenantConfiguration? = null
    ): Tenant {
        val tenantSlug = TenantSlug.fromString(slug)

        require(!tenantPort.existsBySlug(tenantSlug)) {
            "Tenant with slug '${tenantSlug.value}' already exists"
        }

        val tenant = Tenant(
            slug = tenantSlug,
            name = TenantName(name),
            subscriptionPlan = plan,
            configuration = initialConfig ?: TenantConfiguration()
        ).activate()

        val savedTenant = tenantPort.save(tenant)

        eventPublisher.publish(
            TenantProvisionedEvent(
                tenantSlug = savedTenant.slug.value,
                tenantName = savedTenant.name.value,
                adminEmail = adminEmail,
                subscriptionPlan = plan.name
            )
        )

        return savedTenant
    }

    @Cacheable(value = [CACHE_TENANT_METADATA], key = "#id.value")
    @Transactional(readOnly = true)
    fun getTenant(id: TenantId): Tenant {
        return tenantPort.findById(id)
            ?: throw TenantNotFoundException("Tenant with ID $id not found")
    }

    @Cacheable(value = [CACHE_TENANT_METADATA], key = "#slug")
    @Transactional(readOnly = true)
    fun getTenantBySlug(slug: String): Tenant {
        return tenantPort.findBySlug(TenantSlug.fromString(slug))
            ?: throw TenantNotFoundException("Tenant with slug '$slug' not found")
    }

    /**
     * Get tenant configuration - cached for performance
     */
    @Cacheable(value = [CACHE_TENANT_CONFIG], key = "#id.value")
    @Transactional(readOnly = true)
    fun getTenantConfiguration(id: TenantId): TenantConfiguration {
        return getTenant(id).configuration
    }

    @Caching(
        evict = [
            CacheEvict(value = [CACHE_TENANT_METADATA], key = "#id.value"),
            CacheEvict(value = [CACHE_TENANT_CONFIG], key = "#id.value", allEntries = false)
        ]
    )
    fun activateTenant(id: TenantId): Tenant {
        val tenant = getTenant(id)
        return tenantPort.save(tenant.activate())
    }

    @Caching(
        evict = [
            CacheEvict(value = [CACHE_TENANT_METADATA], key = "#id.value"),
            CacheEvict(value = [CACHE_TENANT_CONFIG], key = "#id.value", allEntries = false)
        ]
    )
    fun suspendTenant(id: TenantId): Tenant {
        val tenant = getTenant(id)
        return tenantPort.save(tenant.suspend())
    }

    @Caching(
        evict = [
            CacheEvict(value = [CACHE_TENANT_METADATA], key = "#id.value"),
            CacheEvict(value = [CACHE_TENANT_CONFIG], key = "#id.value", allEntries = false)
        ]
    )
    fun updateTenantConfiguration(
        id: TenantId,
        language: String? = null,
        timezone: String? = null,
        features: Map<String, Boolean>? = null
    ): Tenant {
        val tenant = getTenant(id)
        val oldConfig = tenant.configuration

        val newConfig = tenant.configuration.copy(
            defaultLanguage = language?.let { Language.fromCode(it) }
                ?: tenant.configuration.defaultLanguage,
            timezone = timezone ?: tenant.configuration.timezone,
            features = features?.let { updateFeatureFlags(tenant.configuration.features, it) }
                ?: tenant.configuration.features
        )

        val savedTenant = tenantPort.save(tenant.updateConfiguration(newConfig))

        // Publish event for audit trail
        val changes = mutableMapOf<String, Pair<String?, String?>>()
        if (language != null) changes["language"] = Pair(oldConfig.defaultLanguage.code, language)
        if (timezone != null) changes["timezone"] = Pair(oldConfig.timezone, timezone)

        if (changes.isNotEmpty()) {
            eventPublisher.publish(
                TenantConfigurationUpdatedEvent(
                    tenantId = id,
                    changedSettings = changes
                )
            )
        }

        return savedTenant
    }

    @CacheEvict(value = [CACHE_TENANT_METADATA], key = "#id.value")
    fun updateTenantBranding(
        id: TenantId,
        primaryColor: String? = null,
        logoUrl: String? = null,
        appName: String? = null
    ): Tenant {
        val tenant = getTenant(id)

        val newBranding = tenant.branding.copy(
            primaryColor = primaryColor ?: tenant.branding.primaryColor,
            logoUrl = logoUrl ?: tenant.branding.logoUrl,
            appName = appName ?: tenant.branding.appName
        )

        return tenantPort.save(tenant.updateBranding(newBranding))
    }

    @Caching(
        evict = [
            CacheEvict(value = [CACHE_TENANT_METADATA], key = "#id.value"),
            CacheEvict(value = [CACHE_TENANT_CONFIG], key = "#id.value", allEntries = false)
        ]
    )
    fun updateWhatsAppConfiguration(
        id: TenantId,
        phoneNumberId: String? = null,
        accessToken: String? = null,
        welcomeMessage: String? = null
    ): Tenant {
        val tenant = getTenant(id)

        val currentWhatsApp = tenant.configuration.whatsAppConfig ?: WhatsAppConfiguration()
        val newWhatsApp = currentWhatsApp.copy(
            phoneNumberId = phoneNumberId ?: currentWhatsApp.phoneNumberId,
            accessToken = accessToken ?: currentWhatsApp.accessToken,
            welcomeMessage = welcomeMessage ?: currentWhatsApp.welcomeMessage
        )

        val newConfig = tenant.configuration.copy(whatsAppConfig = newWhatsApp)
        return tenantPort.save(tenant.updateConfiguration(newConfig))
    }

    @Transactional(readOnly = true)
    fun listAllTenants(): List<Tenant> = tenantPort.findAll()

    @Transactional(readOnly = true)
    fun listActiveTenants(): List<Tenant> = tenantPort.findActive()

    private fun updateFeatureFlags(
        current: FeatureFlags,
        updates: Map<String, Boolean>
    ): FeatureFlags = current.copy(
        whatsAppIntegration = updates["whatsAppIntegration"] ?: current.whatsAppIntegration,
        advancedReporting = updates["advancedReporting"] ?: current.advancedReporting,
        customBranding = updates["customBranding"] ?: current.customBranding,
        apiAccess = updates["apiAccess"] ?: current.apiAccess,
        photoEvidence = updates["photoEvidence"] ?: current.photoEvidence,
        gpsTracking = updates["gpsTracking"] ?: current.gpsTracking
    )
}

class TenantNotFoundException(message: String) : RuntimeException(message)
class TenantAlreadyExistsException(message: String) : RuntimeException(message)
