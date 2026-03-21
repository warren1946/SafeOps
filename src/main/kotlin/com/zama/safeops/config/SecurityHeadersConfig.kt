/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 * Permission granted for use, modification, and distribution with attribution.
 * No warranty provided.
 */

package com.zama.safeops.config

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.filter.OncePerRequestFilter

/**
 * Security headers filter to add additional security headers to all responses.
 */
@Configuration
class SecurityHeadersConfig {

    @Bean
    fun securityHeadersFilter(): OncePerRequestFilter {
        return object : OncePerRequestFilter() {
            override fun doFilterInternal(
                request: HttpServletRequest,
                response: HttpServletResponse,
                filterChain: FilterChain
            ) {
                // Add security headers
                response.setHeader("X-Content-Type-Options", "nosniff")
                response.setHeader("X-Frame-Options", "DENY")
                response.setHeader("X-XSS-Protection", "1; mode=block")
                response.setHeader("Strict-Transport-Security", "max-age=31536000; includeSubDomains")
                response.setHeader(
                    "Content-Security-Policy",
                    "default-src 'self'; script-src 'self' 'unsafe-inline' 'unsafe-eval' https://cdn.jsdelivr.net; style-src 'self' 'unsafe-inline' https://cdn.jsdelivr.net; img-src 'self' data: https:; font-src 'self' https://cdn.jsdelivr.net;"
                )
                response.setHeader("Referrer-Policy", "strict-origin-when-cross-origin")
                response.setHeader("Permissions-Policy", "geolocation=(), microphone=(), camera=()")

                // Remove server information
                response.setHeader("Server", "SafeOps")

                filterChain.doFilter(request, response)
            }
        }
    }
}
