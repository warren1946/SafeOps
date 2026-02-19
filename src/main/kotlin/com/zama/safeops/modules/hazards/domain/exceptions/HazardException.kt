/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 * Permission granted for use, modification, and distribution with attribution.
 * No warranty provided.
 */

package com.zama.safeops.modules.hazards.domain.exceptions

import com.zama.safeops.modules.shared.exceptions.ApiException
import org.springframework.http.HttpStatus

sealed class HazardException(
    override val code: String,
    override val httpStatus: HttpStatus,
    override val userMessage: String
) : RuntimeException(userMessage), ApiException