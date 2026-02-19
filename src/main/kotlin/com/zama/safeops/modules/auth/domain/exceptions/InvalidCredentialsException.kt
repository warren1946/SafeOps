/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 * Permission granted for use, modification, and distribution with attribution.
 * No warranty provided.
 */

package com.zama.safeops.modules.auth.domain.exceptions

import com.zama.safeops.modules.shared.constants.ErrorCodes
import org.springframework.http.HttpStatus

class InvalidCredentialsException : AuthException(
    code = ErrorCodes.AUTH_INVALID_CREDENTIALS,
    httpStatus = HttpStatus.UNAUTHORIZED,
    userMessage = "Invalid email or password"
)