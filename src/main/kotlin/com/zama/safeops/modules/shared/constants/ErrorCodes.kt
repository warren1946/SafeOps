/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 * Permission granted for use, modification, and distribution with attribution.
 * No warranty provided.
 */

package com.zama.safeops.modules.shared.constants

object ErrorCodes {

    const val GENERIC = "GENERIC_500"
    const val NOT_FOUND = "GENERIC_404"

    const val VALIDATION = "VALIDATION_001"
    const val INVALID_INPUT = "VALIDATION_002"

    const val AUTH_USER_EXISTS = "AUTH_001"
    const val AUTH_INVALID_CREDENTIALS = "AUTH_002"
    const val AUTH_INVALID_TOKEN = "AUTH_003"
    const val AUTH_EXPIRED_TOKEN = "AUTH_004"
    const val AUTH_NOT_FOUND = "AUTH_005"

    const val CORE_NOT_FOUND = "CORE_404"
    const val CORE_CONFLICT = "CORE_409"
    const val CORE_DUPLICATE = "CORE_409_DUPLICATE"

    const val HAZARD_NOT_FOUND = "HAZARD_404"
    const val HAZARD_INVALID_INPUT = "HAZARD_001"
    const val HAZARD_ASSIGNMENT_FAILED = "HAZARD_002"
    const val HAZARD_UPDATE_NOT_ALLOWED = "HAZARD_003"

    const val INSPECTION_NOT_FOUND = "INSPECTION_NOT_FOUND"
    const val INSPECTION_ITEM_NOT_FOUND = "INSPECTION_ITEM_NOT_FOUND"
    const val INSPECTION_INVALID_INPUT = "INSPECTION_INVALID_INPUT"
    const val INSPECTION_INVALID_STATE = "INSPECTION_INVALID_STATE"
    const val INSPECTION_MAPPING_ERROR = "INSPECTION_MAPPING_ERROR"

    const val SAFETY_EVENT_NOT_FOUND = "SAFETY_EVENT_NOT_FOUND"
    const val SAFETY_ALERT_NOT_FOUND = "SAFETY_ALERT_NOT_FOUND"
    const val SAFETY_REPORT_NOT_FOUND = "SAFETY_REPORT_NOT_FOUND"
    const val SAFETY_INVALID_INPUT = "SAFETY_INVALID_INPUT"
    const val SAFETY_INVALID_STATE = "SAFETY_INVALID_STATE"
}