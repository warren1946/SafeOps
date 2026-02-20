/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 * Permission granted for use, modification, and distribution with attribution.
 * No warranty provided.
 */

package com.zama.safeops.modules.safety.application.exceptions

import com.zama.safeops.modules.shared.constants.ErrorCodes
import org.springframework.http.HttpStatus

class SafetyInvalidInputException(message: String) : SafetyException(
    code = ErrorCodes.SAFETY_INVALID_INPUT,
    httpStatus = HttpStatus.BAD_REQUEST,
    userMessage = message
)