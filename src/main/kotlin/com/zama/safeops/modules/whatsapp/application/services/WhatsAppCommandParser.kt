/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 * Permission granted for use, modification, and distribution with attribution.
 * No warranty provided.
 */

package com.zama.safeops.modules.whatsapp.application.services

import com.zama.safeops.modules.whatsapp.domain.model.WhatsAppCommand
import com.zama.safeops.modules.whatsapp.domain.model.WhatsAppMessage
import org.springframework.stereotype.Service

/**
 * Parses incoming WhatsApp messages into commands.
 * Supports both structured commands and natural language parsing.
 */
@Service
class WhatsAppCommandParser {

    /**
     * Parse a WhatsApp message into a command.
     */
    fun parse(message: WhatsAppMessage): WhatsAppCommand {
        val text = message.text?.trim() ?: ""

        // Try to match using the enum's fromText method
        return WhatsAppCommand.fromText(text)
            ?: parseWithKeywords(text)
            ?: WhatsAppCommand.GET_HELP
    }

    /**
     * Parse using keyword matching for more flexible input.
     */
    private fun parseWithKeywords(text: String): WhatsAppCommand? {
        val normalized = text.lowercase()

        return when {
            // Start inspection patterns
            normalized.contains("start") && normalized.contains("inspection") ->
                WhatsAppCommand.START_INSPECTION

            normalized == "start" ->
                WhatsAppCommand.START_INSPECTION

            // Hazard report patterns
            normalized.contains("hazard") || normalized.contains("danger") ||
                    normalized.contains("unsafe") || normalized.contains("incident") ->
                WhatsAppCommand.REPORT_HAZARD

            normalized.contains("report") ->
                WhatsAppCommand.REPORT_HAZARD

            // Status check patterns
            normalized.contains("status") || normalized.contains("progress") ||
                    normalized.contains("my inspections") || normalized.contains("completed") ->
                WhatsAppCommand.CHECK_STATUS

            // Help patterns
            normalized.contains("help") || normalized.contains("how") ||
                    normalized.contains("?") || normalized.contains("menu") ->
                WhatsAppCommand.GET_HELP

            // Emergency patterns
            normalized.contains("emergency") || normalized.contains("urgent") ||
                    normalized.contains("accident") || normalized.contains("injured") ||
                    normalized.contains("🚨") ->
                WhatsAppCommand.EMERGENCY

            // Cancel patterns
            normalized.contains("cancel") || normalized.contains("stop") ||
                    normalized.contains("exit") || normalized.contains("back") ->
                WhatsAppCommand.CANCEL

            else -> null
        }
    }

    /**
     * Extract template code from inspection start message.
     * Example: "START INSPECTION SHAFT-A" -> "SHAFT-A"
     */
    fun extractTemplateCode(message: WhatsAppMessage): String? {
        val text = message.text?.trim() ?: return null
        val parts = text.split(" ", limit = 3)
        return if (parts.size >= 3) parts[2] else null
    }

    /**
     * Extract location code from check-in or hazard report.
     */
    fun extractLocationCode(message: WhatsAppMessage): String? {
        val text = message.text?.trim() ?: return null
        val parts = text.split(" ", limit = 3)
        return when {
            parts.size >= 2 && parts[0].lowercase() in listOf("checkin", "at", "location") -> parts[1]
            parts.size >= 3 && parts[0].lowercase() == "hazard" -> parts[1]
            else -> null
        }
    }

    /**
     * Parse a yes/no answer.
     */
    fun parseYesNoAnswer(text: String?): Boolean? {
        return when (text?.trim()?.lowercase()) {
            "yes", "y", "yeah", "yep", "true", "1", "✅", "👍" -> true
            "no", "n", "nah", "nope", "false", "0", "❌", "👎" -> false
            else -> null
        }
    }

    /**
     * Parse severity rating (1-5).
     */
    fun parseSeverityRating(text: String?): Int? {
        val normalized = text?.trim()
        return when {
            normalized.isNullOrEmpty() -> null
            normalized.matches(Regex("^[1-5]$")) -> normalized.toInt()
            normalized.contains("critical") || normalized.contains("severe") -> 5
            normalized.contains("high") || normalized.contains("serious") -> 4
            normalized.contains("medium") || normalized.contains("moderate") -> 3
            normalized.contains("low") || normalized.contains("minor") -> 2
            normalized.contains("very low") || normalized.contains("minimal") -> 1
            else -> null
        }
    }
}
