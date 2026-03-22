/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 */

package com.zama.safeops.config

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * SpringDoc OpenAPI configuration to handle Kotlin-specific issues.
 * Disabled by default - use OpenApiConfig for main configuration.
 */
@Configuration
@ConditionalOnProperty(name = ["springdoc.customizer.enabled"], havingValue = "true", matchIfMissing = false)
class SpringDocConfig {

    /**
     * Customizer to configure the OpenAPI object.
     * Only enabled if explicitly configured to avoid bean conflicts.
     */
    @Bean
    fun customOpenAPI(): OpenAPI {
        return OpenAPI()
            .info(
                Info()
                    .title("SafeOps API")
                    .description(
                        "Mining Safety Management Platform - REST API Documentation\n\n" +
                                "Note: All authenticated endpoints require a Bearer token in the Authorization header."
                    )
                    .version("v1.0.0")
            )
    }
}
