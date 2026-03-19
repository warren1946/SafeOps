/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 */

package com.zama.safeops.modules.tenant.api.controllers

import com.zama.safeops.modules.shared.api.ApiController
import com.zama.safeops.modules.shared.api.ApiResponse
import com.zama.safeops.modules.shared.api.PagedResponse
import com.zama.safeops.modules.tenant.application.services.TenantService
import com.zama.safeops.modules.tenant.domain.model.Tenant
import com.zama.safeops.modules.tenant.domain.valueobjects.SubscriptionPlan
import com.zama.safeops.modules.tenant.domain.valueobjects.TenantId
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/admin/tenants")
@PreAuthorize("hasRole('SUPER_ADMIN')")
class TenantController(
    private val tenantService: TenantService
) : ApiController() {

    @PostMapping
    fun provisionTenant(
        @Valid @RequestBody request: ProvisionTenantRequest
    ): ResponseEntity<ApiResponse<TenantResponse>> {
        val tenant = tenantService.provisionTenant(
            slug = request.slug,
            name = request.name,
            adminEmail = request.adminEmail,
            plan = SubscriptionPlan.valueOf(request.plan.uppercase())
        )
        return created("Tenant provisioned successfully", tenant.toResponse())
    }

    @GetMapping("/{id}")
    fun getTenant(@PathVariable id: Long): ResponseEntity<ApiResponse<TenantResponse>> {
        val tenant = tenantService.getTenant(TenantId(id))
        return ok("Tenant retrieved", tenant.toResponse())
    }

    @GetMapping
    fun listTenants(): ResponseEntity<PagedResponse<TenantResponse>> {
        val tenants = tenantService.listAllTenants().map { it.toResponse() }
        return paged(tenants, tenants.size.toLong(), 1, tenants.size)
    }

    @PutMapping("/{id}/activate")
    fun activateTenant(@PathVariable id: Long): ResponseEntity<ApiResponse<TenantResponse>> {
        val tenant = tenantService.activateTenant(TenantId(id))
        return ok("Tenant activated", tenant.toResponse())
    }

    @PutMapping("/{id}/suspend")
    fun suspendTenant(@PathVariable id: Long): ResponseEntity<ApiResponse<TenantResponse>> {
        val tenant = tenantService.suspendTenant(TenantId(id))
        return ok("Tenant suspended", tenant.toResponse())
    }

    @PutMapping("/{id}/configuration")
    fun updateConfiguration(
        @PathVariable id: Long,
        @Valid @RequestBody request: UpdateConfigurationRequest
    ): ResponseEntity<ApiResponse<TenantResponse>> {
        val tenant = tenantService.updateTenantConfiguration(
            id = TenantId(id),
            language = request.language,
            timezone = request.timezone,
            features = request.features
        )
        return ok("Configuration updated", tenant.toResponse())
    }

    @PutMapping("/{id}/branding")
    fun updateBranding(
        @PathVariable id: Long,
        @Valid @RequestBody request: UpdateBrandingRequest
    ): ResponseEntity<ApiResponse<TenantResponse>> {
        val tenant = tenantService.updateTenantBranding(
            id = TenantId(id),
            primaryColor = request.primaryColor,
            logoUrl = request.logoUrl,
            appName = request.appName
        )
        return ok("Branding updated", tenant.toResponse())
    }

    @PutMapping("/{id}/whatsapp")
    fun updateWhatsAppConfig(
        @PathVariable id: Long,
        @Valid @RequestBody request: UpdateWhatsAppConfigRequest
    ): ResponseEntity<ApiResponse<TenantResponse>> {
        val tenant = tenantService.updateWhatsAppConfiguration(
            id = TenantId(id),
            phoneNumberId = request.phoneNumberId,
            accessToken = request.accessToken,
            welcomeMessage = request.welcomeMessage
        )
        return ok("WhatsApp configuration updated", tenant.toResponse())
    }
}

// ==================== DTOs ====================

data class ProvisionTenantRequest(
    @field:NotBlank @field:Size(min = 3, max = 50)
    val slug: String,

    @field:NotBlank @field:Size(max = 200)
    val name: String,

    @field:NotBlank
    val adminEmail: String,

    val plan: String = "BASIC"
)

data class UpdateConfigurationRequest(
    val language: String? = null,
    val timezone: String? = null,
    val features: Map<String, Boolean>? = null
)

data class UpdateBrandingRequest(
    val primaryColor: String? = null,
    val logoUrl: String? = null,
    val appName: String? = null
)

data class UpdateWhatsAppConfigRequest(
    val phoneNumberId: String? = null,
    val accessToken: String? = null,
    val welcomeMessage: String? = null
)

data class TenantResponse(
    val id: Long,
    val slug: String,
    val name: String,
    val status: String,
    val plan: String,
    val configuration: ConfigurationResponse,
    val branding: BrandingResponse,
    val createdAt: String,
    val activatedAt: String?
)

data class ConfigurationResponse(
    val defaultLanguage: String,
    val supportedLanguages: List<String>,
    val timezone: String,
    val features: FeatureFlagsResponse
)

data class FeatureFlagsResponse(
    val whatsAppIntegration: Boolean,
    val advancedReporting: Boolean,
    val customBranding: Boolean,
    val apiAccess: Boolean,
    val photoEvidence: Boolean,
    val gpsTracking: Boolean
)

data class BrandingResponse(
    val primaryColor: String,
    val logoUrl: String?,
    val appName: String
)

// ==================== Mappers ====================

fun Tenant.toResponse(): TenantResponse = TenantResponse(
    id = this.id?.value ?: 0,
    slug = this.slug.value,
    name = this.name.value,
    status = this.status.name,
    plan = this.subscriptionPlan.name,
    configuration = ConfigurationResponse(
        defaultLanguage = this.configuration.defaultLanguage.code,
        supportedLanguages = this.configuration.supportedLanguages.map { it.code },
        timezone = this.configuration.timezone,
        features = FeatureFlagsResponse(
            whatsAppIntegration = this.configuration.features.whatsAppIntegration,
            advancedReporting = this.configuration.features.advancedReporting,
            customBranding = this.configuration.features.customBranding,
            apiAccess = this.configuration.features.apiAccess,
            photoEvidence = this.configuration.features.photoEvidence,
            gpsTracking = this.configuration.features.gpsTracking
        )
    ),
    branding = BrandingResponse(
        primaryColor = this.branding.primaryColor,
        logoUrl = this.branding.logoUrl,
        appName = this.branding.appName
    ),
    createdAt = this.createdAt.toString(),
    activatedAt = this.activatedAt?.toString()
)
