/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 * Permission granted for use, modification, and distribution with attribution.
 * No warranty provided.
 */

package com.zama.safeops.modules.auth.infrastructure.security

import com.zama.safeops.modules.auth.application.services.PasswordEncoderPort
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component

@Component
class PasswordEncoderAdapter(private val delegate: PasswordEncoder) : PasswordEncoderPort {

    override fun encode(raw: String): String = requireNotNull(delegate.encode(raw)) { "PasswordEncoder returned null" }

    override fun matches(raw: String, encoded: String): Boolean = delegate.matches(raw, encoded)
}