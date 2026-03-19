/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 */

package com.zama.safeops.modules.i18n.application

import com.zama.safeops.modules.tenant.domain.model.Language
import com.zama.safeops.modules.tenant.domain.valueobjects.TenantId
import org.springframework.stereotype.Service
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

/**
 * Service for internationalization and localization.
 * Supports multiple languages per tenant with fallbacks.
 */
@Service
class LocalizationService(
    private val messageProvider: MessageProvider
) {

    fun getMessage(
        key: String,
        tenantId: TenantId? = null,
        language: Language = Language.ENGLISH,
        vararg args: Any
    ): String {
        return messageProvider.getMessage(tenantId, language, key, args)
    }

    fun getWhatsAppMessage(
        templateId: String,
        tenantId: TenantId,
        language: Language,
        vararg args: Any
    ): String {
        return messageProvider.getMessage(tenantId, language, "whatsapp.$templateId", args)
    }

    fun getNotificationSubject(
        notificationType: String,
        tenantId: TenantId,
        language: Language,
        vararg args: Any
    ): String {
        return messageProvider.getMessage(tenantId, language, "notification.$notificationType.subject", args)
    }

    fun getNotificationBody(
        notificationType: String,
        tenantId: TenantId,
        language: Language,
        vararg args: Any
    ): String {
        return messageProvider.getMessage(tenantId, language, "notification.$notificationType.body", args)
    }

    fun getSupportedLanguages(tenantId: TenantId): List<Language> {
        return messageProvider.getSupportedLanguages(tenantId)
    }

    fun formatDate(date: LocalDate, language: Language, pattern: String? = null): String {
        val formatter = DateTimeFormatter.ofPattern(pattern ?: "yyyy-MM-dd")
        return date.format(formatter)
    }

    fun formatDateTime(dateTime: Instant, language: Language, timezone: String = "UTC"): String {
        val zoned = dateTime.atZone(ZoneId.of(timezone))
        return zoned.toLocalDateTime().toString()
    }
}

/**
 * Port for message retrieval.
 */
interface MessageProvider {
    fun getMessage(tenantId: TenantId?, language: Language, key: String, args: Array<out Any>): String
    fun getSupportedLanguages(tenantId: TenantId): List<Language>
}

/**
 * In-memory implementation with fallback to resource bundles.
 */
@Service
class ResourceBundleMessageProvider : MessageProvider {

    private val defaultMessages = mapOf(
        // WhatsApp Messages
        "whatsapp.welcome" to mapOf(
            "en" to "Welcome to SafeOps! Send START INSPECTION to begin.",
            "pt" to "Bem-vindo ao SafeOps! Envie INICIAR INSPEÇÃO para começar.",
            "fr" to "Bienvenue sur SafeOps! Envoyez DÉMARRER INSPECTION pour commencer.",
            "sw" to "Karibu SafeOps! Tuma ANZA UKAGUZI kuanza.",
            "af" to "Welkom by SafeOps! Stuur BEGIN INSPEKSIE om te begin.",
            "zu" to "Siyakwamukela ku-SafeOps! Thumela QALA UKUHLOLA ukuze uqale."
        ),
        "whatsapp.inspection.started" to mapOf(
            "en" to "Starting inspection for {0}. Question {1}/{2}: {3}",
            "pt" to "Iniciando inspeção para {0}. Questão {1}/{2}: {3}",
            "fr" to "Démarrage de l'inspection pour {0}. Question {1}/{2}: {3}",
            "sw" to "Kuanza ukaguzi kwa {0}. Swali {1}/{2}: {3}",
            "af" to "Begin inspeksie vir {0}. Vraag {1}/{2}: {3}",
            "zu" to "Iqala ukuhlola ku-{0}. Umbuzo {1}/{2}: {3}"
        ),
        "whatsapp.inspection.completed" to mapOf(
            "en" to "✅ Inspection completed! Score: {0}%. Thank you, {1}.",
            "pt" to "✅ Inspeção concluída! Pontuação: {0}%. Obrigado, {1}.",
            "fr" to "✅ Inspection terminée! Score: {0}%. Merci, {1}.",
            "sw" to "✅ Ukaguzi umekamilika! Alama: {0}%. Asante, {1}.",
            "af" to "✅ Inspeksie voltooi! Telling: {0}%. Dankie, {1}.",
            "zu" to "✅ Ukuhlola kuqediwe! Isikali: {0}%. Ngiyabonga, {1}."
        ),
        "whatsapp.hazard.received" to mapOf(
            "en" to "⚠️ Hazard reported. Reference: {0}. Severity: {1}/5. Stay safe!",
            "pt" to "⚠️ Perigo reportado. Referência: {0}. Gravidade: {1}/5. Mantenha-se seguro!",
            "fr" to "⚠️ Danger signalé. Référence: {0}. Gravité: {1}/5. Restez en sécurité!",
            "sw" to "⚠️ Hatari imeripotiwa. Kumbukumbu: {0}. Ukali: {1}/5. Kuwa salama!",
            "af" to "⚠️ Gevaar aangemeld. Verwysing: {0}. Ernstigheid: {1}/5. Bly veilig!",
            "zu" to "⚠️ Ingozi ibikiwe. I-referensi: {0}. Ubunzima: {1}/5. Hlala uphephile!"
        ),

        // Notification Subjects
        "notification.SAFETY_ALERT_CRITICAL.subject" to mapOf(
            "en" to "🚨 CRITICAL SAFETY ALERT - {0}",
            "pt" to "🚨 ALERTA CRÍTICO DE SEGURANÇA - {0}",
            "fr" to "🚨 ALERTE SÉCURITÉ CRITIQUE - {0}",
            "sw" to "🚨 ONYO LA USALAMA LA MUHIMU - {0}",
            "af" to "🚨 KRITIESE VEILIGHEIDSALERT - {0}",
            "zu" to "🚨 ISEXA ELIPHAKEME LOKUQAPHELA - {0}"
        ),
        "notification.INSPECTION_COMPLETED.subject" to mapOf(
            "en" to "Inspection Completed - {0}",
            "pt" to "Inspeção Concluída - {0}",
            "fr" to "Inspection Terminée - {0}",
            "sw" to "Ukaguzi Umekamilika - {0}",
            "af" to "Inspeksie Voltooi - {0}",
            "zu" to "Ukuhlola Kuqediwe - {0}"
        ),

        // Notification Bodies
        "notification.INSPECTION_COMPLETED.body" to mapOf(
            "en" to "Officer {0} completed inspection at {1} with score {2}%.",
            "pt" to "Oficial {0} completou inspeção em {1} com pontuação {2}%.",
            "fr" to "L'agent {0} a terminé l'inspection à {1} avec un score de {2}%.",
            "sw" to "Afisa {0} amemaliza ukaguzi katika {1} na alama ya {2}%.",
            "af" to "Beampte {0} het inspeksie by {1} voltooi met telling van {2}%.",
            "zu" to "Iphoyisa {0} liquqedile ukuhlola ku-{1} ngesikali se-{2}%."
        )
    )

    override fun getMessage(tenantId: TenantId?, language: Language, key: String, args: Array<out Any>): String {
        val languageMessages = defaultMessages[key]
        val message = languageMessages?.get(language.code)
            ?: languageMessages?.get("en")
            ?: "[$key]"

        return if (args.isEmpty()) message else message.format(*args)
    }

    override fun getSupportedLanguages(tenantId: TenantId): List<Language> {
        return Language.entries.toList()
    }
}
