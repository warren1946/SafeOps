/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 * Permission granted for use, modification, and distribution with attribution.
 * No warranty provided.
 */

package com.zama.safeops.modules.shared.api

import com.fasterxml.jackson.databind.exc.InvalidDefinitionException
import com.fasterxml.jackson.databind.exc.InvalidFormatException
import com.fasterxml.jackson.databind.exc.MismatchedInputException
import com.zama.safeops.modules.shared.constants.ErrorCodes
import io.swagger.v3.oas.annotations.Hidden
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.HttpMediaTypeNotSupportedException
import org.springframework.web.HttpRequestMethodNotSupportedException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.MissingPathVariableException
import org.springframework.web.bind.MissingServletRequestParameterException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException
import org.springframework.web.multipart.MaxUploadSizeExceededException
import org.springframework.web.multipart.MultipartException
import org.springframework.web.servlet.resource.NoResourceFoundException

@Hidden
@RestControllerAdvice
class GlobalExceptionHandler {

    // JSON parsing / malformed body / null for non-nullable Kotlin fields
    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleUnreadableJson(ex: HttpMessageNotReadableException): ResponseEntity<ApiResponse<Unit>> {

        val exMessage = ex.message ?: ""
        val message = when {
            exMessage.contains("missing", ignoreCase = true) ->
                "Request body is missing required fields"

            exMessage.contains("Cannot deserialize", ignoreCase = true) ->
                "Invalid request format"

            exMessage.contains("Unexpected character", ignoreCase = true) ||
                    exMessage.contains("Unexpected end-of-input", ignoreCase = true) ->
                "Malformed JSON syntax"

            ex.cause is InvalidFormatException ->
                "Invalid data type"

            ex.cause is MismatchedInputException ->
                "Invalid input structure"

            ex.cause is InvalidDefinitionException ->
                "Invalid field definition"

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
    fun handleMissingParam(ex: MissingServletRequestParameterException): ResponseEntity<ApiResponse<Unit>> =
        ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(
                ApiResponse.error(
                    code = ErrorCodes.INVALID_INPUT,
                    message = "Missing required parameter: ${ex.parameterName}"
                )
            )

    // Missing path variables
    @ExceptionHandler(MissingPathVariableException::class)
    fun handleMissingPathVariable(ex: MissingPathVariableException): ResponseEntity<ApiResponse<Unit>> =
        ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(
                ApiResponse.error(
                    code = ErrorCodes.INVALID_INPUT,
                    message = "Missing required path variable: ${ex.variableName}"
                )
            )

    // Wrong type for path variables or query params
    @ExceptionHandler(MethodArgumentTypeMismatchException::class)
    fun handleTypeMismatch(ex: MethodArgumentTypeMismatchException): ResponseEntity<ApiResponse<Unit>> {
        val message = when (ex.requiredType) {
            Number::class.java, Int::class.java, Long::class.java ->
                "Invalid numeric value for parameter '${ex.name}'"

            Boolean::class.java ->
                "Invalid boolean value for parameter '${ex.name}'"

            else ->
                "Invalid value for parameter '${ex.name}'"
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

    // Unsupported content type (e.g., sending XML instead of JSON)
    @ExceptionHandler(HttpMediaTypeNotSupportedException::class)
    fun handleUnsupportedMediaType(ex: HttpMediaTypeNotSupportedException): ResponseEntity<ApiResponse<Unit>> =
        ResponseEntity
            .status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
            .body(
                ApiResponse.error(
                    code = ErrorCodes.INVALID_INPUT,
                    message = "Unsupported content type: ${ex.contentType}. Use application/json"
                )
            )

    // Unsupported HTTP method
    @ExceptionHandler(HttpRequestMethodNotSupportedException::class)
    fun handleMethodNotSupported(ex: HttpRequestMethodNotSupportedException): ResponseEntity<ApiResponse<Unit>> =
        ResponseEntity
            .status(HttpStatus.METHOD_NOT_ALLOWED)
            .body(
                ApiResponse.error(
                    code = ErrorCodes.GENERIC,
                    message = "Method '${ex.method}' not supported for this endpoint"
                )
            )

    // Bean validation errors (@Valid)
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidation(ex: MethodArgumentNotValidException): ResponseEntity<ApiResponse<Unit>> {
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

    // Payload too large
    @ExceptionHandler(MaxUploadSizeExceededException::class)
    fun handleMaxUploadSizeExceeded(ex: MaxUploadSizeExceededException): ResponseEntity<ApiResponse<Unit>> =
        ResponseEntity
            .status(HttpStatus.PAYLOAD_TOO_LARGE)
            .body(
                ApiResponse.error(
                    code = ErrorCodes.PAYLOAD_TOO_LARGE,
                    message = "File size exceeds maximum allowed limit"
                )
            )

    // Multipart errors
    @ExceptionHandler(MultipartException::class)
    fun handleMultipartException(ex: MultipartException): ResponseEntity<ApiResponse<Unit>> =
        ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(
                ApiResponse.error(
                    code = ErrorCodes.INVALID_INPUT,
                    message = "Invalid multipart request: ${ex.message}"
                )
            )

    // Generic invalid input (thrown manually)
    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgument(ex: IllegalArgumentException): ResponseEntity<ApiResponse<Unit>> =
        ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(
                ApiResponse.error(
                    code = ErrorCodes.INVALID_INPUT,
                    message = ex.message ?: "Invalid input"
                )
            )

    // No resource found
    @ExceptionHandler(NoResourceFoundException::class)
    fun handleNoResourceFound(ex: NoResourceFoundException): ResponseEntity<ApiResponse<Unit>> =
        ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(
                ApiResponse.error(
                    code = ErrorCodes.NOT_FOUND,
                    message = "The requested endpoint does not exist"
                )
            )

    // Catch-all fallback
    @ExceptionHandler(Exception::class)
    fun handleGeneric(ex: Exception): ResponseEntity<ApiResponse<Unit>> {
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
}
