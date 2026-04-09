/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 */

package com.zama.safeops.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

/**
 * Security validation configuration properties.
 */
@Configuration
@ConfigurationProperties(prefix = "app.security.validation")
class SecurityValidationProperties {

    /** Maximum request body size in bytes (default: 10MB) */
    var maxRequestSize: Long = 10 * 1024 * 1024

    /** Maximum string field length */
    var maxStringLength: Int = 10000

    /** Maximum array/collection size */
    var maxCollectionSize: Int = 1000

    /** Maximum nested depth for JSON */
    var maxNestingDepth: Int = 20

    /** Enable XSS sanitization */
    var enableXssSanitization: Boolean = true

    /** Enable SQL injection detection */
    var enableSqlInjectionCheck: Boolean = true

    /** Enable command injection detection */
    var enableCommandInjectionCheck: Boolean = true

    /** Blocked characters/patterns (regex) */
    var blockedPatterns: List<String> = listOf(
        "<script[^>]*>[\\s\\S]*?</script>",
        "javascript:",
        "on\\w+\\s*=",
        "<iframe",
        "<object",
        "<embed"
    )
}

/**
 * Input sanitization utility for cleaning user input.
 */
object InputSanitizer {

    private val XSS_PATTERN = Regex("<script[^>]*>[\\s\\S]*?</script>", RegexOption.IGNORE_CASE)
    private val EVENT_HANDLER_PATTERN = Regex("on\\w+\\s*=", RegexOption.IGNORE_CASE)
    private val JAVASCRIPT_PROTOCOL_PATTERN = Regex("javascript:", RegexOption.IGNORE_CASE)
    private val IFRAME_PATTERN = Regex("<iframe", RegexOption.IGNORE_CASE)
    private val OBJECT_PATTERN = Regex("<object", RegexOption.IGNORE_CASE)
    private val EMBED_PATTERN = Regex("<embed", RegexOption.IGNORE_CASE)
    private val HTML_TAG_PATTERN = Regex("<[^>]+>", RegexOption.IGNORE_CASE)

    /**
     * Sanitizes a string by removing XSS vectors.
     */
    fun sanitize(input: String?): String? {
        if (input.isNullOrBlank()) return input

        return input
            .replace(XSS_PATTERN, "")
            .replace(EVENT_HANDLER_PATTERN, "")
            .replace(JAVASCRIPT_PROTOCOL_PATTERN, "")
            .replace(IFRAME_PATTERN, "")
            .replace(OBJECT_PATTERN, "")
            .replace(EMBED_PATTERN, "")
            .trim()
    }

    /**
     * Sanitizes a string by completely stripping HTML.
     */
    fun stripHtml(input: String?): String? {
        if (input.isNullOrBlank()) return input
        return input.replace(HTML_TAG_PATTERN, "").trim()
    }

    /**
     * Escapes HTML special characters.
     */
    fun escapeHtml(input: String?): String? {
        if (input.isNullOrBlank()) return input
        return input
            .replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;")
            .replace("'", "&#x27;")
            .replace("/", "&#x2F;")
    }

    /**
     * Validates if input contains potential SQL injection patterns.
     */
    fun containsSqlInjection(input: String?): Boolean {
        if (input.isNullOrBlank()) return false

        val sqlPatterns = listOf(
            "(?i)(\\b(SELECT|INSERT|UPDATE|DELETE|DROP|CREATE|ALTER|EXEC|EXECUTE|UNION|MERGE|TRUNCATE)\\b)",
            "(?i)(--|#|/\\*|\\*/)",
            "(?i)(;\\s*\\b(DROP|DELETE|INSERT|UPDATE)\\b)",
            "(?i)(\\bOR\\b\\s+\\d+\\s*=\\s*\\d+)",
            "(?i)(\\bAND\\b\\s+\\d+\\s*=\\s*\\d+)",
            "(?i)('\\s*OR\\s*'|\"\\s*OR\\s*\"|'\\s*AND\\s*'|\"\\s*AND\\s*\")",
            "(?i)(WAITFOR\\s+DELAY)",
            "(?i)(BULK\\s+INSERT)",
            "(?i)(INTO\\s+OUTFILE)",
            "(?i)(LOAD_FILE)"
        )

        return sqlPatterns.any { pattern ->
            Regex(pattern).containsMatchIn(input)
        }
    }

    /**
     * Validates if input contains potential command injection patterns.
     */
    fun containsCommandInjection(input: String?): Boolean {
        if (input.isNullOrBlank()) return false

        val cmdPatterns = listOf(
            "(?i)(\\b(sh|bash|cmd|powershell|python|ruby|perl)\\s+-[c|e])",
            "(?i)(;\\s*\\b(rm|del|format|shutdown|reboot|mkfs)\\b)",
            "(?i)(\\|\\s*\\b(sh|bash|cmd)\\b)",
            "(?i)(`[^`]+`)",
            "(?i)(\\$\\([^)]+\\))",
            "(?i)(&&\\s*\\b(rm|del|format)\\b)",
            "(?i)(\\|\\s*\\b(nc|netcat|wget|curl)\\b)"
        )

        return cmdPatterns.any { pattern ->
            Regex(pattern).containsMatchIn(input)
        }
    }

    /**
     * Validates if input contains potential path traversal patterns.
     */
    fun containsPathTraversal(input: String?): Boolean {
        if (input.isNullOrBlank()) return false
        return input.contains("..") || input.contains("../") || input.contains("..\\")
    }

    /**
     * Validates if input contains null bytes (indicates potential attack).
     */
    fun containsNullBytes(input: String?): Boolean {
        return input?.contains('\u0000') ?: false
    }

    /**
     * Validates email format strictly.
     */
    fun isValidEmail(email: String?): Boolean {
        if (email.isNullOrBlank()) return false
        val emailRegex = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
        return emailRegex.matches(email) && email.length <= 254
    }

    /**
     * Validates that input contains only alphanumeric and safe characters.
     */
    fun isAlphanumeric(input: String?, allowSpaces: Boolean = false, allowDashUnderscore: Boolean = false): Boolean {
        if (input.isNullOrBlank()) return true
        val pattern = when {
            allowSpaces && allowDashUnderscore -> Regex("^[a-zA-Z0-9\\s_-]+$")
            allowSpaces -> Regex("^[a-zA-Z0-9\\s]+$")
            allowDashUnderscore -> Regex("^[a-zA-Z0-9_-]+$")
            else -> Regex("^[a-zA-Z0-9]+$")
        }
        return pattern.matches(input)
    }
}
