/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 */

package com.zama.safeops.config

import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder

/**
 * Test configuration for common test beans.
 */
@TestConfiguration
class TestConfig {

    @Bean
    @Primary
    fun testPasswordEncoder(): PasswordEncoder = BCryptPasswordEncoder()
}

/**
 * Test constants for reuse across tests.
 */
object TestConstants {
    const val DEFAULT_TENANT_ID = 1L
    const val DEFAULT_USER_ID = 1L
    const val DEFAULT_EMAIL = "test@example.com"
    const val DEFAULT_PASSWORD = "TestPassword123!"
}
