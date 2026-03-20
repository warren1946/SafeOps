/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 */

package com.zama.safeops.config

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.servers.Server
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class OpenApiConfig {

    @Bean
    fun customOpenAPI(): OpenAPI {
        return OpenAPI()
            .info(
                Info()
                    .title("SafeOps API")
                    .description("Mining Safety Management Platform - REST API Documentation")
                    .version("v1.0.0")
            )
            .addServersItem(
                Server()
                    .url("https://safeops-1.onrender.com")
                    .description("Render Production Server")
            )
    }
}
