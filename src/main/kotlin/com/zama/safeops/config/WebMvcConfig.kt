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
        // Rate limiting should be first
        registry.addInterceptor(rateLimitingInterceptor)
            .addPathPatterns("/api/**")
            .order(0)

        // Logging interceptor
        registry.addInterceptor(loggingInterceptor)
            .addPathPatterns("/api/**")
            .order(1)

        // RBAC interceptor
        registry.addInterceptor(roleBasedAccessInterceptor)
            .addPathPatterns("/api/**")
            .order(2)
    }

    override fun addCorsMappings(registry: CorsRegistry) {
        registry.addMapping("/api/**")
            .allowedOrigins("*")  // Configure appropriately for production
            .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")
            .allowedHeaders("*")
            .exposedHeaders("X-Rate-Limit-Remaining", "X-Rate-Limit-Retry-After-Millis")
            .maxAge(3600)
    }
}
