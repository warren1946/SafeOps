/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 */

package com.zama.safeops.config

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary

/**
 * Jackson JSON configuration.
 */
@Configuration
class JacksonConfig {

    @Bean
    @Primary
    fun objectMapper(): ObjectMapper {
        return ObjectMapper()
    }
}
