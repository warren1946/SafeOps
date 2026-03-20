/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 */

package com.zama.safeops.config

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType
import io.swagger.v3.oas.annotations.security.SecurityScheme
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Contact
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.info.License
import io.swagger.v3.oas.models.servers.Server
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@SecurityScheme(
    name = "bearerAuth",
    type = SecuritySchemeType.HTTP,
    bearerFormat = "JWT",
    scheme = "bearer"
)
class OpenApiConfig {

    @Bean
    fun customOpenAPI(): OpenAPI {
        return OpenAPI()
            .info(
                Info()
                    .title("SafeOps API")
                    .description("Mining Safety Management Platform - REST API Documentation")
                    .version("v1.0.0")
                    .contact(
                        Contact()
                            .name("SafeOps Support")
                            .email("support@safeops.com")
                            .url("https://safeops.com")
                    )
                    .license(
                        License()
                            .name("MIT License")
                            .url("https://opensource.org/licenses/MIT")
                    )
            )
            .addServersItem(
                Server()
                    .url("http://localhost:8080")
                    .description("Local Development Server")
            )
            .addServersItem(
                Server()
                    .url("https://api.safeops.com")
                    .description("Production Server")
            )
    }
}
