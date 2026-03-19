/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 */

package com.zama.safeops.modules.tenant.domain.valueobjects

import com.fasterxml.jackson.annotation.JsonValue
import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter

/**
 * Value object representing a unique tenant identifier.
 * Uses inline class for type safety and performance.
 */
@JvmInline
value class TenantId(@JsonValue val value: Long) {
    init {
        require(value > 0) { "TenantId must be positive" }
    }

    companion object {
        fun of(value: Long): TenantId = TenantId(value)
    }
}

/**
 * JPA converter for TenantId.
 */
@Converter(autoApply = true)
class TenantIdConverter : AttributeConverter<TenantId, Long> {
    override fun convertToDatabaseColumn(attribute: TenantId?): Long? = attribute?.value
    override fun convertToEntityAttribute(dbData: Long?): TenantId? = dbData?.let { TenantId(it) }
}

/**
 * Value object for tenant slug (URL-friendly identifier).
 */
@JvmInline
value class TenantSlug(val value: String) {
    init {
        require(value.matches(Regex("^[a-z0-9]+(?:-[a-z0-9]+)*$"))) {
            "Tenant slug must be lowercase alphanumeric with hyphens only"
        }
        require(value.length in 3..50) {
            "Tenant slug must be between 3 and 50 characters"
        }
    }

    companion object {
        fun fromString(value: String): TenantSlug = TenantSlug(value.lowercase().trim())
    }
}

/**
 * Value object for tenant display name.
 */
@JvmInline
value class TenantName(val value: String) {
    init {
        require(value.isNotBlank()) { "Tenant name cannot be blank" }
        require(value.length <= 200) { "Tenant name cannot exceed 200 characters" }
    }
}

/**
 * Subscription plan types for white-label clients.
 */
enum class SubscriptionPlan {
    BASIC,
    PROFESSIONAL,
    ENTERPRISE,
    CUSTOM
}

/**
 * Tenant status lifecycle.
 */
enum class TenantStatus {
    PENDING,        // Tenant created but not yet activated
    ACTIVE,         // Fully operational
    SUSPENDED,      // Temporarily disabled
    CANCELLED       // Permanently closed
}
