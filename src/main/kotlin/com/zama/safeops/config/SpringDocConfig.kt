/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 */

package com.zama.safeops.config

import org.springdoc.core.customizers.OpenApiCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * SpringDoc OpenAPI configuration to handle Kotlin-specific issues.
 */
@Configuration
class SpringDocConfig {

    /**
     * Customizer to configure the OpenAPI object.
     */
    @Bean
    fun openApiCustomizer(): OpenApiCustomizer {
        return OpenApiCustomizer { openApi ->
            // Ensure info description is not null before appending
            val currentDescription = openApi.info?.description ?: ""
            openApi.info.description = "$currentDescription\n\n" +
                    "Note: All authenticated endpoints require a Bearer token in the Authorization header."
        }
    }
}
