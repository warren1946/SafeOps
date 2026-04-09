/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 */

package com.zama.safeops.config

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.servers.Server
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class OpenApiConfig(
    @Value("\${app.api.server-url:}") private val serverUrl: String,
    @Value("\${app.api.server-description:Production Server}") private val serverDescription: String
) {

    @Bean
    fun customOpenAPI(): OpenAPI {
        val openApi = OpenAPI()
            .info(
                Info()
                    .title("SafeOps API")
                    .description("Mining Safety Management Platform - REST API Documentation")
                    .version("v1.0.0")
            )

        // Add server URL if configured, otherwise use relative URL
        val url = serverUrl.takeIf { it.isNotBlank() } ?: "/"
        openApi.addServersItem(
            Server()
                .url(url)
                .description(serverDescription)
        )

        return openApi
    }
}
