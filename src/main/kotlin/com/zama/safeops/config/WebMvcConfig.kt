/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 * Permission granted for use, modification, and distribution with attribution.
 * No warranty provided.
 */

package com.zama.safeops.config

import com.zama.safeops.modules.auth.infrastructure.rbac.RoleBasedAccessInterceptor
import com.zama.safeops.modules.shared.logging.LoggingInterceptor
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class WebMvcConfig(
    private val roleBasedAccessInterceptor: RoleBasedAccessInterceptor,
    private val rateLimitingInterceptor: RateLimitingInterceptor,
    private val loggingInterceptor: LoggingInterceptor
) : WebMvcConfigurer {

    override fun addInterceptors(registry: InterceptorRegistry) {
        // Rate limiting should be first - applies to all endpoints including /health
        registry.addInterceptor(rateLimitingInterceptor)
            .addPathPatterns("/api/**", "/health")
            .excludePathPatterns("/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**", "/webjars/**")
            .order(0)

        // Logging interceptor
        registry.addInterceptor(loggingInterceptor)
            .addPathPatterns("/api/**")
            .excludePathPatterns("/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**", "/webjars/**")
            .order(1)

        // RBAC interceptor - only applies to /api/** so no need for swagger exclusions
        registry.addInterceptor(roleBasedAccessInterceptor)
            .addPathPatterns("/api/**")
            .order(2)
    }

    override fun addCorsMappings(registry: CorsRegistry) {
        registry.addMapping("/api/**")
            .allowedOrigins("*")  // Configure appropriately for production
            .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")
            .allowedHeaders("*")
            .exposedHeaders("X-Rate-Limit-Limit", "X-Rate-Limit-Remaining", "X-Rate-Limit-Reset", "X-Rate-Limit-Retry-After")
            .maxAge(3600)
    }
}
