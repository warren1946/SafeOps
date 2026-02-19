/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 * Permission granted for use, modification, and distribution with attribution.
 * No warranty provided.
 */

package com.zama.safeops.modules.shared.api

import com.zama.safeops.modules.shared.constants.ErrorCodes
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.HttpMediaTypeNotSupportedException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.MissingServletRequestParameterException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException
import org.springframework.web.servlet.resource.NoResourceFoundException

@RestControllerAdvice
class GlobalExceptionHandler {

    // JSON parsing / malformed body / null for non-nullable Kotlin fields
    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleUnreadableJson(ex: HttpMessageNotReadableException): ResponseEntity<ApiResponse<Nothing>> {

        val message = when {
            ex.message?.contains("missing") == true ->
                "Request body is missing required fields"

            ex.message?.contains("Cannot deserialize") == true ->
                "Invalid request format"

            else -> "Malformed JSON request"
        }

        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(
                ApiResponse.error(
                    code = ErrorCodes.INVALID_INPUT,
                    message = message
                )
            )
    }

    // Missing query or form parameters
    @ExceptionHandler(MissingServletRequestParameterException::class)
    fun handleMissingParam(ex: MissingServletRequestParameterException): ResponseEntity<ApiResponse<Nothing>> =
        ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(
                ApiResponse.error(
                    code = ErrorCodes.INVALID_INPUT,
                    message = "Missing required parameter: ${ex.parameterName}"
                )
            )

    // Wrong type for path variables or query params
    @ExceptionHandler(MethodArgumentTypeMismatchException::class)
    fun handleTypeMismatch(ex: MethodArgumentTypeMismatchException): ResponseEntity<ApiResponse<Nothing>> =
        ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(
                ApiResponse.error(
                    code = ErrorCodes.INVALID_INPUT,
                    message = "Invalid value for parameter '${ex.name}'"
                )
            )

    // Unsupported content type (e.g., sending XML instead of JSON)
    @ExceptionHandler(HttpMediaTypeNotSupportedException::class)
    fun handleUnsupportedMediaType(ex: HttpMediaTypeNotSupportedException): ResponseEntity<ApiResponse<Nothing>> =
        ResponseEntity
            .status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
            .body(
                ApiResponse.error(
                    code = ErrorCodes.INVALID_INPUT,
                    message = "Unsupported content type: ${ex.contentType}"
                )
            )

    // Bean validation errors (@Valid)
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidation(ex: MethodArgumentNotValidException): ResponseEntity<ApiResponse<Nothing>> {
        val errors = ex.bindingResult.fieldErrors.associate {
            it.field to (it.defaultMessage ?: "Invalid value")
        }

        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(
                ApiResponse.error(
                    code = ErrorCodes.VALIDATION,
                    message = "Validation failed",
                    errors = errors
                )
            )
    }

    // Generic invalid input (thrown manually)
    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgument(ex: IllegalArgumentException): ResponseEntity<ApiResponse<Nothing>> =
        ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(
                ApiResponse.error(
                    code = ErrorCodes.INVALID_INPUT,
                    message = ex.message ?: "Invalid input"
                )
            )

    // Catch-all fallback
    @ExceptionHandler(Exception::class)
    fun handleGeneric(ex: Exception): ResponseEntity<ApiResponse<Nothing>> {
        ex.printStackTrace()

        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(
                ApiResponse.error(
                    code = ErrorCodes.GENERIC,
                    message = "Internal server error"
                )
            )
    }

    // Catch no resource found
    @ExceptionHandler(NoResourceFoundException::class)
    fun handleNoResourceFound(ex: NoResourceFoundException): ResponseEntity<ApiResponse<Nothing>> = ResponseEntity
        .status(HttpStatus.NOT_FOUND)
        .body(
            ApiResponse.error(
                code = ErrorCodes.NOT_FOUND,
                message = "The requested endpoint does not exist"
            )
        )
}