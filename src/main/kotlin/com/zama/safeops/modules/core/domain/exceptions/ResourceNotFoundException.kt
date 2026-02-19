/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 * Permission granted for use, modification, and distribution with attribution.
 * No warranty provided.
 */

package com.zama.safeops.modules.core.domain.exceptions

import com.zama.safeops.modules.shared.constants.ErrorCodes
import org.springframework.http.HttpStatus

class ResourceNotFoundException(resource: String) : CoreException(
    code = ErrorCodes.CORE_NOT_FOUND,
    httpStatus = HttpStatus.NOT_FOUND,
    userMessage = "$resource not found"
)