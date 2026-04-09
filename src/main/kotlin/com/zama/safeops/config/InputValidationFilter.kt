/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 */

package com.zama.safeops.config

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.zama.safeops.modules.shared.constants.ErrorCodes
import jakarta.servlet.FilterChain
import jakarta.servlet.ReadListener
import jakarta.servlet.ServletInputStream
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletRequestWrapper
import jakarta.servlet.http.HttpServletResponse
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import java.io.BufferedReader
import java.io.ByteArrayInputStream
import java.io.IOException
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets

/**
 * Filter that validates all incoming requests for:
 * - Payload size limits
 * - XSS attempts
 * - SQL injection attempts
 * - Command injection attempts
 * - Path traversal attempts
 * - Malformed JSON
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 1) // After rate limiting, before other filters
class InputValidationFilter(
    private val properties: SecurityValidationProperties,
    private val objectMapper: ObjectMapper
) : OncePerRequestFilter() {

    companion object {
        private val JSON_CONTENT_TYPES = setOf(
            MediaType.APPLICATION_JSON_VALUE,
            "application/json;charset=UTF-8",
            "application/json;charset=utf-8"
        )
    }

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        // Skip validation for certain paths
        if (shouldSkipValidation(request.requestURI)) {
            filterChain.doFilter(request, response)
            return
        }

        try {
            // Check content length
            val contentLength = request.contentLengthLong
            if (contentLength > properties.maxRequestSize) {
                rejectRequest(response, "Request body too large", ErrorCodes.PAYLOAD_TOO_LARGE)
                return
            }

            // Wrap request to allow multiple reads
            val wrappedRequest = CachedBodyHttpServletRequest(request)

            // Validate body content if it's JSON
            if (isJsonContent(request)) {
                val body = wrappedRequest.body

                if (body.isNotBlank()) {
                    // Check for null bytes
                    if (InputSanitizer.containsNullBytes(body)) {
                        rejectRequest(response, "Invalid characters in request", ErrorCodes.INVALID_INPUT)
                        return
                    }

                    // Parse and validate JSON structure
                    try {
                        val jsonNode = objectMapper.readTree(body)

                        // Check nesting depth
                        if (getNestingDepth(jsonNode) > properties.maxNestingDepth) {
                            rejectRequest(response, "JSON nesting too deep", ErrorCodes.INVALID_INPUT)
                            return
                        }

                        // Check collection sizes
                        if (hasOversizedCollections(jsonNode)) {
                            rejectRequest(response, "Collection size exceeds maximum", ErrorCodes.INVALID_INPUT)
                            return
                        }

                        // Validate string fields
                        val validationError = validateJsonNode(jsonNode)
                        if (validationError != null) {
                            rejectRequest(response, validationError, ErrorCodes.INVALID_INPUT)
                            return
                        }

                    } catch (e: Exception) {
                        rejectRequest(response, "Malformed JSON: ${e.message}", ErrorCodes.INVALID_INPUT)
                        return
                    }
                }
            }

            // Validate query parameters
            val queryParamError = validateQueryParameters(request)
            if (queryParamError != null) {
                rejectRequest(response, queryParamError, ErrorCodes.INVALID_INPUT)
                return
            }

            // Validate headers
            val headerError = validateHeaders(request)
            if (headerError != null) {
                rejectRequest(response, headerError, ErrorCodes.INVALID_INPUT)
                return
            }

            filterChain.doFilter(wrappedRequest, response)

        } catch (e: IOException) {
            rejectRequest(response, "Error reading request: ${e.message}", ErrorCodes.INVALID_INPUT)
        }
    }

    private fun shouldSkipValidation(uri: String): Boolean {
        return uri.startsWith("/actuator") ||
                uri.startsWith("/swagger-ui") ||
                uri.startsWith("/v3/api-docs") ||
                uri.startsWith("/health")
    }

    private fun isJsonContent(request: HttpServletRequest): Boolean {
        val contentType = request.contentType ?: return false
        return JSON_CONTENT_TYPES.any { contentType.contains(it) }
    }

    private fun getNestingDepth(node: JsonNode, currentDepth: Int = 0): Int {
        if (currentDepth > properties.maxNestingDepth) return currentDepth

        return when {
            node.isObject -> {
                var maxDepth = currentDepth
                node.fields().forEach { entry ->
                    val depth = getNestingDepth(entry.value, currentDepth + 1)
                    if (depth > maxDepth) maxDepth = depth
                }
                maxDepth
            }

            node.isArray -> {
                var maxDepth = currentDepth
                (node as ArrayNode).forEach { element ->
                    val depth = getNestingDepth(element, currentDepth + 1)
                    if (depth > maxDepth) maxDepth = depth
                }
                maxDepth
            }

            else -> currentDepth
        }
    }

    private fun hasOversizedCollections(node: JsonNode): Boolean {
        return when {
            node.isArray -> {
                if ((node as ArrayNode).size() > properties.maxCollectionSize) return true
                node.any { hasOversizedCollections(it) }
            }

            node.isObject -> {
                node.fields().asSequence().any { hasOversizedCollections(it.value) }
            }

            else -> false
        }
    }

    private fun validateJsonNode(node: JsonNode, fieldName: String = "root"): String? {
        return when {
            node.isTextual -> {
                val text = node.asText()

                // Check string length
                if (text.length > properties.maxStringLength) {
                    return "Field '$fieldName' exceeds maximum length of ${properties.maxStringLength}"
                }

                // Check for SQL injection
                if (properties.enableSqlInjectionCheck && InputSanitizer.containsSqlInjection(text)) {
                    return "Potentially malicious content detected in field '$fieldName'"
                }

                // Check for command injection
                if (properties.enableCommandInjectionCheck && InputSanitizer.containsCommandInjection(text)) {
                    return "Potentially malicious content detected in field '$fieldName'"
                }

                // Check for path traversal
                if (InputSanitizer.containsPathTraversal(text)) {
                    return "Invalid characters in field '$fieldName'"
                }

                null
            }

            node.isObject -> {
                for (entry in (node as ObjectNode).fields()) {
                    val error = validateJsonNode(entry.value, entry.key)
                    if (error != null) return error
                }
                null
            }

            node.isArray -> {
                (node as ArrayNode).forEachIndexed { index, element ->
                    val error = validateJsonNode(element, "$fieldName[$index]")
                    if (error != null) return error
                }
                null
            }

            else -> null
        }
    }

    private fun validateQueryParameters(request: HttpServletRequest): String? {
        val parameterNames = request.parameterNames
        while (parameterNames.hasMoreElements()) {
            val name = parameterNames.nextElement()
            val values = request.getParameterValues(name) ?: continue

            for (value in values) {
                if (value.length > properties.maxStringLength) {
                    return "Query parameter '$name' exceeds maximum length"
                }
                if (InputSanitizer.containsNullBytes(value)) {
                    return "Invalid characters in query parameter '$name'"
                }
                if (properties.enableSqlInjectionCheck && InputSanitizer.containsSqlInjection(value)) {
                    return "Potentially malicious content in query parameter '$name'"
                }
            }
        }
        return null
    }

    private fun validateHeaders(request: HttpServletRequest): String? {
        val headerNames = request.headerNames
        while (headerNames.hasMoreElements()) {
            val name = headerNames.nextElement()
            val value = request.getHeader(name) ?: continue

            if (value.length > properties.maxStringLength) {
                return "Header '$name' exceeds maximum length"
            }
            if (InputSanitizer.containsNullBytes(value)) {
                return "Invalid characters in header '$name'"
            }
        }
        return null
    }

    private fun rejectRequest(response: HttpServletResponse, message: String, code: String) {
        response.status = HttpStatus.BAD_REQUEST.value()
        response.contentType = MediaType.APPLICATION_JSON_VALUE
        response.writer.write(
            """{"success":false,"error":{"code":"$code","message":"$message"},"timestamp":"${java.time.Instant.now()}"}"""
        )
    }
}

/**
 * Request wrapper that caches the body for multiple reads.
 */
class CachedBodyHttpServletRequest(request: HttpServletRequest) : HttpServletRequestWrapper(request) {

    val body: String = request.inputStream.use { it.readBytes().toString(StandardCharsets.UTF_8) }
    private val cachedBody: ByteArray = body.toByteArray(StandardCharsets.UTF_8)

    override fun getInputStream(): ServletInputStream {
        return CachedBodyServletInputStream(cachedBody)
    }

    override fun getReader(): BufferedReader {
        return BufferedReader(InputStreamReader(inputStream, StandardCharsets.UTF_8))
    }
}

/**
 * Input stream that reads from cached body.
 */
class CachedBodyServletInputStream(private val cachedBody: ByteArray) : ServletInputStream() {

    private val inputStream = ByteArrayInputStream(cachedBody)

    override fun read(): Int = inputStream.read()

    override fun isFinished(): Boolean = inputStream.available() == 0

    override fun isReady(): Boolean = true

    override fun setReadListener(listener: ReadListener?) {
        throw UnsupportedOperationException("ReadListener not supported")
    }
}
