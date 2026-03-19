/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 */

package com.zama.safeops.modules.whatsapp.domain.model

import com.zama.safeops.modules.tenant.domain.valueobjects.TenantId
import java.time.Instant
import java.time.temporal.ChronoUnit

/**
 * Represents a WhatsApp conversation session with a user.
 * Uses State pattern for managing conversation flow.
 */
data class WhatsAppConversation(
    val id: ConversationId? = null,
    val tenantId: TenantId,
    val phoneNumber: String,
    val userId: Long? = null,
    val officerName: String? = null,
    val state: ConversationState = ConversationState.IDLE,
    val context: ConversationContext = ConversationContext(),
    val startedAt: Instant = Instant.now(),
    val lastActivityAt: Instant = Instant.now(),
    val expiresAt: Instant = Instant.now().plus(30, ChronoUnit.MINUTES),
    val messageCount: Int = 0
) {
    fun isExpired(): Boolean = Instant.now().isAfter(expiresAt)

    fun touch(): WhatsAppConversation = copy(
        lastActivityAt = Instant.now(),
        expiresAt = Instant.now().plus(30, ChronoUnit.MINUTES),
        messageCount = messageCount + 1
    )

    fun transitionTo(newState: ConversationState, newContext: ConversationContext? = null): WhatsAppConversation {
        return copy(
            state = newState,
            context = newContext ?: context,
            lastActivityAt = Instant.now()
        )
    }

    fun withUser(userId: Long, officerName: String): WhatsAppConversation {
        return copy(userId = userId, officerName = officerName)
    }
}

@JvmInline
value class ConversationId(val value: String)

/**
 * States for the WhatsApp conversation state machine.
 */
enum class ConversationState {
    IDLE,                           // No active conversation
    AUTHENTICATING,                 // Waiting for officer identification
    MAIN_MENU,                      // Main command menu

    // Inspection workflow states
    INSPECTION_SELECTING_LOCATION,  // Selecting mine/site/shaft/area
    INSPECTION_SELECTING_TEMPLATE,  // Selecting inspection template
    INSPECTION_IN_PROGRESS,         // Active inspection with questions
    INSPECTION_ANSWERING_QUESTION,  // Waiting for answer to current question
    INSPECTION_ADDING_PHOTO,        // Waiting for photo upload
    INSPECTION_ADDING_COMMENT,      // Waiting for additional comment
    INSPECTION_CONFIRMING_SUBMIT,   // Confirming inspection submission

    // Hazard workflow states
    HAZARD_REPORTING_LOCATION,      // Selecting hazard location
    HAZARD_DESCRIBING,              // Describing the hazard
    HAZARD_RATING_SEVERITY,         // Rating hazard severity (1-5)
    HAZARD_ADDING_PHOTO,            // Waiting for hazard photo
    HAZARD_CONFIRMING,              // Confirming hazard report

    // General states
    AWAITING_HELP_TOPIC,            // Selecting help topic
    EMERGENCY_CONFIRMING            // Confirming emergency report
}

/**
 * Context data for the conversation state machine.
 */
data class ConversationContext(
    val selectedMineId: Long? = null,
    val selectedSiteId: Long? = null,
    val selectedShaftId: Long? = null,
    val selectedAreaId: Long? = null,
    val selectedTemplateId: Long? = null,
    val activeInspectionId: Long? = null,
    val currentQuestionIndex: Int = 0,
    val answers: List<QuestionAnswer> = emptyList(),
    val hazardDescription: String? = null,
    val hazardSeverity: Int? = null,
    val pendingPhotos: List<String> = emptyList(),
    val language: String = "en"
)

data class QuestionAnswer(
    val questionId: Long,
    val question: String,
    val answer: String,
    val requiresPhoto: Boolean = false,
    val photoUrl: String? = null,
    val comment: String? = null
)

/**
 * Represents an incoming WhatsApp message.
 */
data class WhatsAppMessage(
    val messageId: String,
    val from: String,
    val text: String? = null,
    val mediaUrl: String? = null,
    val mediaType: MediaType? = null,
    val timestamp: Instant = Instant.now(),
    val location: GeoLocation? = null
)

data class GeoLocation(
    val latitude: Double,
    val longitude: Double
)

enum class MediaType {
    IMAGE,
    VIDEO,
    AUDIO,
    DOCUMENT
}

/**
 * Represents an outgoing WhatsApp message.
 */
data class WhatsAppResponse(
    val to: String,
    val text: String,
    val type: ResponseType = ResponseType.TEXT,
    val buttons: List<QuickReplyButton> = emptyList(),
    val mediaUrl: String? = null
)

data class QuickReplyButton(
    val id: String,
    val title: String
)

enum class ResponseType {
    TEXT,
    IMAGE,
    DOCUMENT,
    INTERACTIVE_BUTTONS,
    INTERACTIVE_LIST
}

/**
 * Commands that can be sent via WhatsApp.
 */
enum class WhatsAppCommand(val trigger: String, val description: String) {
    START_INSPECTION("START INSPECTION", "Begin a new safety inspection"),
    REPORT_HAZARD("REPORT HAZARD", "Report a safety hazard"),
    CHECK_STATUS("STATUS", "Check inspection status"),
    GET_HELP("HELP", "Get help information"),
    EMERGENCY("EMERGENCY", "Report an emergency"),
    CANCEL("CANCEL", "Cancel current operation"),
    MENU("MENU", "Show main menu");

    companion object {
        fun fromText(text: String): WhatsAppCommand? {
            val normalized = text.trim().uppercase()
            return entries.find {
                normalized == it.trigger || normalized == it.name.replace("_", " ")
            }
        }
    }
}
