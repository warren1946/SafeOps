/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 * Permission granted for use, modification, and distribution with attribution.
 * No warranty provided.
 */

package com.zama.safeops.modules.inspections.application.exceptions

import com.zama.safeops.modules.shared.constants.ErrorCodes
import org.springframework.http.HttpStatus

class InspectionItemNotFoundException(id: Long) : InspectionException(
    code = ErrorCodes.INSPECTION_ITEM_NOT_FOUND,
    httpStatus = HttpStatus.NOT_FOUND,
    userMessage = "Inspection item with ID $id not found"
)