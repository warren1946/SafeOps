/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 */

package com.zama.safeops.config

import com.github.benmanes.caffeine.cache.Caffeine
import org.springframework.cache.CacheManager
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.EnableCaching
import org.springframework.cache.caffeine.CaffeineCacheManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.Scheduled
import java.util.concurrent.TimeUnit

/**
 * Cache configuration using Caffeine.
 *
 * Cache regions:
 * - tenant-config: Tenant configuration (1 hour TTL)
 * - tenant-metadata: Tenant basic info (30 min TTL)
 * - templates: Inspection templates (2 hours TTL)
 * - user-sessions: User session data (15 min TTL)
 * - dashboard-stats: Dashboard statistics (5 min TTL)
 * - location-hierarchy: Mine/Site/Shaft/Area hierarchy (10 min TTL)
 *
 * Future: Add Redis as L2 cache for distributed deployments
 */
@Configuration
@EnableCaching
class CacheConfig {

    @Bean
    fun cacheManager(): CacheManager {
        return CaffeineCacheManager().apply {
            setCaffeine(
                Caffeine.newBuilder()
                    .maximumSize(10_000)
                    .recordStats()
            )
            // Define cache-specific configurations
            setCacheNames(
                listOf(
                    CACHE_TENANT_CONFIG,
                    CACHE_TENANT_METADATA,
                    CACHE_TEMPLATES,
                    CACHE_USER_SESSIONS,
                    CACHE_DASHBOARD_STATS,
                    CACHE_LOCATION_HIERARCHY,
                    CACHE_INSPECTION_SUMMARY
                )
            )
        }
    }

    /**
     * Tenant configuration cache - Long TTL as configs change infrequently
     */
    @Bean("tenantConfigCache")
    fun tenantConfigCache(): Caffeine<Any, Any> {
        return Caffeine.newBuilder()
            .maximumSize(1_000)
            .expireAfterWrite(1, TimeUnit.HOURS)
            .recordStats()
    }

    /**
     * Dashboard statistics cache - Short TTL for near real-time data
     */
    @Bean("dashboardStatsCache")
    fun dashboardStatsCache(): Caffeine<Any, Any> {
        return Caffeine.newBuilder()
            .maximumSize(500)
            .expireAfterWrite(5, TimeUnit.MINUTES)
            .refreshAfterWrite(1, TimeUnit.MINUTES)
            .recordStats()
    }

    /**
     * Template cache - Templates change rarely
     */
    @Bean("templateCache")
    fun templateCache(): Caffeine<Any, Any> {
        return Caffeine.newBuilder()
            .maximumSize(2_000)
            .expireAfterWrite(2, TimeUnit.HOURS)
            .recordStats()
    }

    /**
     * Location hierarchy cache - Changes when structure is modified
     */
    @Bean("locationHierarchyCache")
    fun locationHierarchyCache(): Caffeine<Any, Any> {
        return Caffeine.newBuilder()
            .maximumSize(500)
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .recordStats()
    }

    companion object {
        const val CACHE_TENANT_CONFIG = "tenant-config"
        const val CACHE_TENANT_METADATA = "tenant-metadata"
        const val CACHE_TEMPLATES = "templates"
        const val CACHE_USER_SESSIONS = "user-sessions"
        const val CACHE_DASHBOARD_STATS = "dashboard-stats"
        const val CACHE_LOCATION_HIERARCHY = "location-hierarchy"
        const val CACHE_INSPECTION_SUMMARY = "inspection-summary"
    }
}

/**
 * Cache eviction scheduler - Clear dashboard stats periodically for fresh data
 */
@Configuration
class CacheEvictionScheduler {

    @Scheduled(fixedRate = 5 * 60 * 1000) // Every 5 minutes
    @CacheEvict(value = [CacheConfig.CACHE_DASHBOARD_STATS], allEntries = true)
    fun evictDashboardStats() {
        // Dashboard stats are automatically cleared
    }

    @Scheduled(cron = "0 0 * * * *") // Every hour
    @CacheEvict(value = [CacheConfig.CACHE_TENANT_CONFIG], allEntries = true)
    fun evictTenantConfigs() {
        // Tenant configs refreshed hourly
    }
}
