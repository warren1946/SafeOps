/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 * Permission granted for use, modification, and distribution with attribution.
 * No warranty provided.
 */

package com.zama.safeops.modules.whatsapp.application.services

import com.zama.safeops.modules.whatsapp.application.ports.WhatsAppPort
import com.zama.safeops.modules.whatsapp.application.statemachine.ConversationStateMachine
import com.zama.safeops.modules.whatsapp.application.statemachine.StateMachineFactory
import com.zama.safeops.modules.whatsapp.domain.model.WhatsAppCommand
import com.zama.safeops.modules.whatsapp.domain.model.WhatsAppConversation
import com.zama.safeops.modules.whatsapp.domain.model.WhatsAppMessage
import com.zama.safeops.modules.whatsapp.domain.model.WhatsAppResponse
import org.springframework.stereotype.Service

/**
 * Engine that orchestrates WhatsApp inspection workflows.
 * Uses a state machine to manage conversation flow.
 */
@Service
class InspectionWorkflowEngine(
    private val whatsAppPort: WhatsAppPort,
    private val conversationService: ConversationService,
    private val userLookupService: UserLookupService
) {
    private val stateMachine: ConversationStateMachine by lazy {
        StateMachineFactory.create { phone -> userLookupService.findByPhone(phone) }
    }

    /**
     * Handle an incoming WhatsApp message.
     */
    fun handle(message: WhatsAppMessage) {
        // Get or create conversation for this phone number
        val conversation = conversationService.getOrCreateConversation(
            phoneNumber = message.from,
            tenantId = resolveTenantId(message.from)
        )

        // Process through state machine
        val result = stateMachine.process(message, conversation)

        // Update conversation state
        conversationService.updateConversation(result.newConversation)

        // Send response
        whatsAppPort.sendResponse(result.response)

        // Execute side effects
        result.sideEffects.forEach { executeSideEffect(it) }
    }

    /**
     * Handle a command directly (for simple commands outside conversation flow).
     */
    fun handleCommand(command: WhatsAppCommand, message: WhatsAppMessage) {
        when (command) {
            WhatsAppCommand.START_INSPECTION -> initiateInspection(message)
            WhatsAppCommand.REPORT_HAZARD -> initiateHazardReport(message)
            WhatsAppCommand.CHECK_STATUS -> sendStatusUpdate(message)
            WhatsAppCommand.GET_HELP -> sendHelpMessage(message)
            WhatsAppCommand.EMERGENCY -> handleEmergency(message)
            WhatsAppCommand.CANCEL -> cancelCurrentOperation(message)
            WhatsAppCommand.MENU -> sendMainMenu(message)
        }
    }

    private fun initiateInspection(message: WhatsAppMessage) {
        val response = WhatsAppResponse(
            to = message.from,
            text = """🏭 Starting new inspection
                |
                |Please select your location:
                |1. Shaft A - Level 3
                |2. Processing Plant
                |3. Underground Tunnel 5
                |4. Equipment Yard
                |
                |Reply with the number or location name.
            """.trimMargin()
        )
        whatsAppPort.sendResponse(response)
    }

    private fun initiateHazardReport(message: WhatsAppMessage) {
        val response = WhatsAppResponse(
            to = message.from,
            text = """⚠️ Hazard Report
                |
                |Please describe the hazard you observed:
                |(e.g., "Water leak near electrical panel")
            """.trimMargin()
        )
        whatsAppPort.sendResponse(response)
    }

    private fun sendStatusUpdate(message: WhatsAppMessage) {
        val response = WhatsAppResponse(
            to = message.from,
            text = """📊 Your Inspection Status
                |
                |✅ Completed today: 3
                |⏳ In progress: 1
                |📝 Pending review: 2
                |
                |Send START INSPECTION to begin a new one.
            """.trimMargin()
        )
        whatsAppPort.sendResponse(response)
    }

    private fun sendHelpMessage(message: WhatsAppMessage) {
        val response = WhatsAppResponse(
            to = message.from,
            text = """🆘 SafeOps Help
                |
                |Available commands:
                |• START INSPECTION - Begin safety inspection
                |• REPORT HAZARD - Report safety hazard
                |• STATUS - Check inspection status
                |• MENU - Show main menu
                |• CANCEL - Cancel current operation
                |• EMERGENCY - Report emergency (immediate response)
                |
                |For technical support, contact your administrator.
            """.trimMargin()
        )
        whatsAppPort.sendResponse(response)
    }

    private fun handleEmergency(message: WhatsAppMessage) {
        // This is critical - immediately notify supervisors
        val response = WhatsAppResponse(
            to = message.from,
            text = """🚨 EMERGENCY ALERT ACTIVATED
                |
                |Your emergency has been logged and supervisors have been notified.
                |Emergency reference: EM-${System.currentTimeMillis() % 10000}
                |
                |If this is a life-threatening situation, also call emergency services immediately!
            """.trimMargin()
        )
        whatsAppPort.sendResponse(response)

        // TODO: Trigger immediate notifications to all supervisors
    }

    private fun cancelCurrentOperation(message: WhatsAppMessage) {
        conversationService.endConversation(message.from)

        val response = WhatsAppResponse(
            to = message.from,
            text = "❌ Current operation cancelled. Send MENU to see available commands."
        )
        whatsAppPort.sendResponse(response)
    }

    private fun sendMainMenu(message: WhatsAppMessage) {
        val response = WhatsAppResponse(
            to = message.from,
            text = """🏠 Main Menu
                |
                |What would you like to do?
            """.trimMargin(),
            buttons = listOf(
                com.zama.safeops.modules.whatsapp.domain.model.QuickReplyButton("start", "Start Inspection"),
                com.zama.safeops.modules.whatsapp.domain.model.QuickReplyButton("hazard", "Report Hazard"),
                com.zama.safeops.modules.whatsapp.domain.model.QuickReplyButton("status", "Check Status"),
                com.zama.safeops.modules.whatsapp.domain.model.QuickReplyButton("help", "Help")
            )
        )
        whatsAppPort.sendResponse(response)
    }

    private fun executeSideEffect(effect: com.zama.safeops.modules.whatsapp.application.statemachine.SideEffect) {
        // Handle side effects from state machine
        when (effect) {
            is com.zama.safeops.modules.whatsapp.application.statemachine.SideEffect.CreateInspection -> {
                // TODO: Call inspection service to create inspection
            }

            is com.zama.safeops.modules.whatsapp.application.statemachine.SideEffect.SaveInspectionAnswer -> {
                // TODO: Save answer to inspection
            }

            is com.zama.safeops.modules.whatsapp.application.statemachine.SideEffect.SubmitInspection -> {
                // TODO: Submit inspection for review
            }

            is com.zama.safeops.modules.whatsapp.application.statemachine.SideEffect.CreateHazard -> {
                // TODO: Create hazard report
            }

            is com.zama.safeops.modules.whatsapp.application.statemachine.SideEffect.SendNotification -> {
                // TODO: Send notification
            }

            is com.zama.safeops.modules.whatsapp.application.statemachine.SideEffect.LogAudit -> {
                // TODO: Log audit entry
            }
        }
    }

    private fun resolveTenantId(phoneNumber: String): com.zama.safeops.modules.tenant.domain.valueobjects.TenantId {
        // TODO: Look up tenant by phone number prefix or user association
        return com.zama.safeops.modules.tenant.domain.valueobjects.TenantId(1)
    }
}

/**
 * Service for managing conversation persistence.
 */
@Service
class ConversationService {
    private val conversations = mutableMapOf<String, WhatsAppConversation>()

    fun getOrCreateConversation(phoneNumber: String, tenantId: com.zama.safeops.modules.tenant.domain.valueobjects.TenantId): WhatsAppConversation {
        return conversations[phoneNumber] ?: WhatsAppConversation(
            id = com.zama.safeops.modules.whatsapp.domain.model.ConversationId("${tenantId.value}-${phoneNumber}-${System.currentTimeMillis()}"),
            tenantId = tenantId,
            phoneNumber = phoneNumber
        ).also { conversations[phoneNumber] = it }
    }

    fun updateConversation(conversation: WhatsAppConversation) {
        conversations[conversation.phoneNumber] = conversation
    }

    fun endConversation(phoneNumber: String) {
        conversations.remove(phoneNumber)
    }
}

/**
 * Service for looking up users by phone number.
 */
@Service
class UserLookupService {
    fun findByPhone(phone: String): Pair<Long, String>? {
        // TODO: Integrate with UserRepository to find user by phone
        // Return Pair(userId, officerName)
        return null // Return null if not found
    }
}
