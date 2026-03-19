/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 */

package com.zama.safeops.modules.whatsapp.application.statemachine

import com.zama.safeops.modules.whatsapp.domain.model.*

/**
 * State machine for managing WhatsApp conversation flows.
 * Implements the State pattern for clean separation of state behaviors.
 */
interface ConversationStateHandler {
    val state: ConversationState
    fun handle(message: WhatsAppMessage, conversation: WhatsAppConversation): StateTransitionResult
}

data class StateTransitionResult(
    val newConversation: WhatsAppConversation,
    val response: WhatsAppResponse,
    val sideEffects: List<SideEffect> = emptyList()
)

/**
 * Side effects that should be executed after state transition.
 */
sealed class SideEffect {
    data class CreateInspection(
        val tenantId: Long,
        val userId: Long,
        val locationId: Long,
        val templateId: Long
    ) : SideEffect()

    data class SaveInspectionAnswer(
        val inspectionId: Long,
        val questionId: Long,
        val answer: String,
        val photoUrl: String?,
        val comment: String?
    ) : SideEffect()

    data class SubmitInspection(val inspectionId: Long) : SideEffect()

    data class CreateHazard(
        val tenantId: Long,
        val reporterId: Long,
        val locationId: Long,
        val description: String,
        val severity: Int,
        val photoUrls: List<String>
    ) : SideEffect()

    data class SendNotification(val notificationType: String, val data: Map<String, Any>) : SideEffect()

    data class LogAudit(val action: String, val details: String) : SideEffect()
}

/**
 * Main state machine that delegates to specific handlers.
 */
class ConversationStateMachine(
    private val handlers: Map<ConversationState, ConversationStateHandler>
) {
    fun process(message: WhatsAppMessage, conversation: WhatsAppConversation): StateTransitionResult {
        val handler = handlers[conversation.state]
            ?: throw IllegalStateException("No handler for state ${conversation.state}")

        return handler.handle(message, conversation)
    }
}

// ==================== State Handlers ====================

/**
 * Idle state - initial state or after conversation expiry.
 */
class IdleStateHandler : ConversationStateHandler {
    override val state = ConversationState.IDLE

    override fun handle(message: WhatsAppMessage, conversation: WhatsAppConversation): StateTransitionResult {
        val command = WhatsAppCommand.fromText(message.text ?: "")

        return if (command != null) {
            // Direct command, skip authentication if needed
            handleCommand(command, conversation)
        } else {
            // No command, show welcome and request authentication
            StateTransitionResult(
                newConversation = conversation.transitionTo(ConversationState.AUTHENTICATING),
                response = WhatsAppResponse(
                    to = message.from,
                    text = """Welcome to SafeOps! 👋
                        |
                        |Please enter your officer ID or registered email to continue.
                        |Send MENU anytime to see available commands.
                    """.trimMargin()
                )
            )
        }
    }

    private fun handleCommand(command: WhatsAppCommand, conversation: WhatsAppConversation): StateTransitionResult {
        return StateTransitionResult(
            newConversation = conversation,
            response = WhatsAppResponse(
                to = conversation.phoneNumber,
                text = "Please authenticate first by entering your officer ID or email."
            )
        )
    }
}

/**
 * Authenticating state - verifying officer identity.
 */
class AuthenticatingStateHandler(
    private val userLookup: (String) -> Pair<Long, String>?
) : ConversationStateHandler {
    override val state = ConversationState.AUTHENTICATING

    override fun handle(message: WhatsAppMessage, conversation: WhatsAppConversation): StateTransitionResult {
        val input = message.text?.trim() ?: ""

        val user = userLookup(input)

        return if (user != null) {
            val (userId, officerName) = user
            StateTransitionResult(
                newConversation = conversation
                    .withUser(userId, officerName)
                    .transitionTo(ConversationState.MAIN_MENU),
                response = WhatsAppResponse(
                    to = message.from,
                    text = """Welcome $officerName! ✅
                        |
                        |Available commands:
                        |• START INSPECTION - Begin safety inspection
                        |• REPORT HAZARD - Report a hazard
                        |• STATUS - Check your inspections
                        |• HELP - Get assistance
                        |• EMERGENCY - Report emergency
                    """.trimMargin(),
                    buttons = listOf(
                        QuickReplyButton("start", "Start Inspection"),
                        QuickReplyButton("hazard", "Report Hazard"),
                        QuickReplyButton("status", "Check Status")
                    )
                )
            )
        } else {
            StateTransitionResult(
                newConversation = conversation,
                response = WhatsAppResponse(
                    to = message.from,
                    text = "Officer not found. Please check your ID or email and try again."
                )
            )
        }
    }
}

/**
 * Main menu state - handling command selection.
 */
class MainMenuStateHandler : ConversationStateHandler {
    override val state = ConversationState.MAIN_MENU

    override fun handle(message: WhatsAppMessage, conversation: WhatsAppConversation): StateTransitionResult {
        val command = WhatsAppCommand.fromText(message.text ?: "")

        return when (command) {
            WhatsAppCommand.START_INSPECTION -> StateTransitionResult(
                newConversation = conversation.transitionTo(ConversationState.INSPECTION_SELECTING_LOCATION),
                response = WhatsAppResponse(
                    to = message.from,
                    text = "Starting new inspection. Please select location:",
                    type = ResponseType.INTERACTIVE_LIST
                ),
                sideEffects = listOf(SideEffect.LogAudit("INSPECTION_STARTED", "Officer ${conversation.userId}"))
            )

            WhatsAppCommand.REPORT_HAZARD -> StateTransitionResult(
                newConversation = conversation.transitionTo(ConversationState.HAZARD_REPORTING_LOCATION),
                response = WhatsAppResponse(
                    to = message.from,
                    text = "Reporting hazard. Please select location where hazard was found:"
                )
            )

            WhatsAppCommand.CHECK_STATUS -> StateTransitionResult(
                newConversation = conversation,
                response = WhatsAppResponse(
                    to = message.from,
                    text = "📊 Your Inspection Status:\n• Completed today: 3\n• In progress: 1\n• Pending review: 2"
                )
            )

            WhatsAppCommand.GET_HELP -> StateTransitionResult(
                newConversation = conversation.transitionTo(ConversationState.AWAITING_HELP_TOPIC),
                response = WhatsAppResponse(
                    to = message.from,
                    text = "What do you need help with?\n1. How to perform inspection\n2. Report emergency\n3. Technical support"
                )
            )

            WhatsAppCommand.EMERGENCY -> StateTransitionResult(
                newConversation = conversation.transitionTo(ConversationState.EMERGENCY_CONFIRMING),
                response = WhatsAppResponse(
                    to = message.from,
                    text = "🚨 EMERGENCY MODE\n\nThis will alert all supervisors immediately.\n\nType CONFIRM to report emergency or CANCEL to abort.",
                    buttons = listOf(
                        QuickReplyButton("confirm_emergency", "CONFIRM EMERGENCY"),
                        QuickReplyButton("cancel", "CANCEL")
                    )
                )
            )

            else -> StateTransitionResult(
                newConversation = conversation,
                response = WhatsAppResponse(
                    to = message.from,
                    text = "I didn't understand. Please send:\n• START INSPECTION\n• REPORT HAZARD\n• STATUS\n• HELP\n• EMERGENCY"
                )
            )
        }
    }
}

/**
 * Factory for creating the complete state machine.
 */
object StateMachineFactory {
    fun create(userLookup: (String) -> Pair<Long, String>?): ConversationStateMachine {
        val handlers = mapOf(
            ConversationState.IDLE to IdleStateHandler(),
            ConversationState.AUTHENTICATING to AuthenticatingStateHandler(userLookup),
            ConversationState.MAIN_MENU to MainMenuStateHandler()
            // Additional handlers would be added here
        )

        return ConversationStateMachine(handlers)
    }
}
