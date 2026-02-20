/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 * Permission granted for use, modification, and distribution with attribution.
 * No warranty provided.
 */

package com.zama.safeops.modules.safety.application.exceptions

import com.zama.safeops.modules.shared.constants.ErrorCodes
import org.springframework.http.HttpStatus

class SafetyReportNotFoundException(id: Long) : SafetyException(
    code = ErrorCodes.SAFETY_REPORT_NOT_FOUND,
    httpStatus = HttpStatus.NOT_FOUND,
    userMessage = "Safety report with ID $id not found"
)