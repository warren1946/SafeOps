/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 */

package com.zama.safeops.modules.ai.application.services

import com.zama.safeops.modules.ai.application.ports.*
import com.zama.safeops.modules.ai.domain.model.DetectedHazard
import com.zama.safeops.modules.ai.domain.model.DetectedSeverity
import com.zama.safeops.modules.hazards.application.ports.HazardPort
import com.zama.safeops.modules.hazards.domain.model.*
import com.zama.safeops.modules.inspections.application.ports.InspectionPort
import com.zama.safeops.modules.inspections.domain.model.InspectionTargetType
import com.zama.safeops.modules.inspections.domain.valueobjects.InspectionId
import com.zama.safeops.modules.safety.domain.model.SafetyLocationType
import com.zama.safeops.modules.tenant.domain.valueobjects.TenantId
import org.springframework.stereotype.Service

/**
 * Service for AI-powered safety analysis.
 * Automatically analyzes inspection photos and creates hazards when issues are detected.
 */
@Service
class AISafetyAnalysisService(
    private val computerVision: ComputerVisionPort,
    private val hazardPort: HazardPort,
    private val inspectionPort: InspectionPort
) {

    /**
     * Analyze an inspection photo for hazards.
     * Auto-creates hazard reports for critical findings.
     */
    fun analyzeInspectionPhoto(
        tenantId: TenantId,
        inspectionId: Long,
        photoUrl: String,
        userId: Long
    ): PhotoAnalysisReport {

        // Get inspection context
        val inspection = inspectionPort.findById(InspectionId(inspectionId))
            ?: throw IllegalArgumentException("Inspection not found: $inspectionId")

        val locationContext = "${inspection.targetType} ${inspection.targetId}"

        // Perform AI analysis
        val analysis = computerVision.analyzeForHazards(
            HazardAnalysisRequest(
                imageUrl = photoUrl,
                locationContext = locationContext,
                tenantId = tenantId,
                inspectionType = inspection.title
            )
        )

        // Map InspectionTargetType to SafetyLocationType
        val safetyLocationType = when (inspection.targetType) {
            InspectionTargetType.AREA -> SafetyLocationType.AREA
            InspectionTargetType.SHAFT -> SafetyLocationType.SHAFT
            InspectionTargetType.SITE -> SafetyLocationType.SITE
        }

        // Auto-create hazards for critical/high severity findings
        val autoCreatedHazards = analysis.hazardsDetected
            .filter { it.severity == DetectedSeverity.HIGH || it.severity == DetectedSeverity.CRITICAL }
            .filter { it.confidence > 0.75 }
            .mapNotNull { hazard ->
                createHazardFromDetection(tenantId, hazard, userId, inspectionId, safetyLocationType, inspection.targetId)
            }

        return PhotoAnalysisReport(
            photoUrl = photoUrl,
            hazardsDetected = analysis.hazardsDetected.size,
            criticalHazards = analysis.hazardsDetected.count { it.severity == DetectedSeverity.CRITICAL },
            autoCreatedHazardIds = autoCreatedHazards,
            requiresManualReview = analysis.hazardsDetected.any { it.confidence in 0.5..0.75 },
            analysis = analysis
        )
    }

    /**
     * Analyze PPE compliance in a photo.
     */
    fun analyzePPECompliance(
        photoUrl: String,
        tenantId: TenantId
    ): PPEComplianceReport {
        val result = computerVision.detectPPE(photoUrl)

        return PPEComplianceReport(
            totalPersons = result.personsDetected,
            compliantPersons = result.compliantPersons,
            violations = result.violations.map { v ->
                PPEViolationReport(
                    personId = v.personId,
                    missingItems = v.missingItems,
                    isCritical = v.missingItems.contains(PPEItem.HARD_HAT) ||
                            v.missingItems.contains(PPEItem.SAFETY_BOOTS)
                )
            },
            complianceRate = result.complianceRate,
            isCompliant = result.complianceRate >= 0.95,
            rawResult = result
        )
    }

    /**
     * Batch analyze multiple photos.
     */
    fun batchAnalyzePhotos(
        tenantId: TenantId,
        photoUrls: List<String>,
        userId: Long
    ): BatchAnalysisReport {
        val reports = photoUrls.map { url ->
            try {
                // Classify first to determine analysis type
                val classification = computerVision.classifyImage(url)

                when (classification.category) {
                    ImageCategory.INSPECTION_PHOTO,
                    ImageCategory.HAZARD_EVIDENCE ->
                        analyzeInspectionPhoto(tenantId, 0, url, userId)

                    ImageCategory.PPE_CHECK ->
                        PhotoAnalysisReport(
                            photoUrl = url,
                            ppeReport = analyzePPECompliance(url, tenantId)
                        )

                    else ->
                        PhotoAnalysisReport(
                            photoUrl = url,
                            classification = classification
                        )
                }
            } catch (e: Exception) {
                PhotoAnalysisReport(
                    photoUrl = url,
                    error = e.message
                )
            }
        }

        return BatchAnalysisReport(
            totalPhotos = photoUrls.size,
            successfulAnalyses = reports.count { it.error == null },
            failedAnalyses = reports.count { it.error != null },
            totalHazardsDetected = reports.sumOf { it.hazardsDetected },
            totalCriticalHazards = reports.sumOf { it.criticalHazards },
            reports = reports
        )
    }

    /**
     * Detect changes between two images (before/after comparison).
     */
    fun detectChanges(
        beforePhotoUrl: String,
        afterPhotoUrl: String
    ): ChangeDetectionReport {
        val result = computerVision.detectChanges(beforePhotoUrl, afterPhotoUrl)

        return ChangeDetectionReport(
            hasChanges = result.changesDetected,
            changeAreas = result.changeAreas.size,
            severity = result.severity,
            description = result.changeAreas.joinToString("; ") { it.description },
            requiresAttention = result.severity == DetectedSeverity.HIGH || result.severity == DetectedSeverity.CRITICAL
        )
    }

    private fun createHazardFromDetection(
        tenantId: TenantId,
        detected: DetectedHazard,
        userId: Long,
        inspectionId: Long,
        locationType: SafetyLocationType,
        locationId: Long
    ): Long? {
        return try {
            val hazard = Hazard(
                id = null,
                title = HazardTitle("AI Detected: ${detected.hazardType.name.replace("_", " ")}"),
                description = HazardDescription(
                    """
                    ${detected.description}
                    
                    Recommended Action: ${detected.recommendedAction}
                    AI Confidence: ${(detected.confidence * 100).toInt()}%
                    Detected in Inspection: #$inspectionId
                """.trimIndent()
                ),
                severity = mapSeverity(detected.severity),
                status = HazardStatus.OPEN,
                createdBy = userId,
                locationType = locationType,
                locationId = locationId
            )

            hazardPort.create(hazard).id?.value
        } catch (e: Exception) {
            null
        }
    }

    private fun mapSeverity(detected: DetectedSeverity): HazardSeverity {
        return when (detected) {
            DetectedSeverity.LOW -> HazardSeverity.LOW
            DetectedSeverity.MEDIUM -> HazardSeverity.MEDIUM
            DetectedSeverity.HIGH -> HazardSeverity.HIGH
            DetectedSeverity.CRITICAL -> HazardSeverity.CRITICAL
        }
    }
}

// Report DTOs
data class PhotoAnalysisReport(
    val photoUrl: String,
    val hazardsDetected: Int = 0,
    val criticalHazards: Int = 0,
    val autoCreatedHazardIds: List<Long> = emptyList(),
    val requiresManualReview: Boolean = false,
    val classification: ImageClassification? = null,
    val ppeReport: PPEComplianceReport? = null,
    val analysis: HazardAnalysisResult? = null,
    val error: String? = null
)

data class PPEComplianceReport(
    val totalPersons: Int,
    val compliantPersons: Int,
    val violations: List<PPEViolationReport>,
    val complianceRate: Double,
    val isCompliant: Boolean,
    val rawResult: PPEComplianceResult
)

data class PPEViolationReport(
    val personId: Int,
    val missingItems: List<PPEItem>,
    val isCritical: Boolean
)

data class BatchAnalysisReport(
    val totalPhotos: Int,
    val successfulAnalyses: Int,
    val failedAnalyses: Int,
    val totalHazardsDetected: Int,
    val totalCriticalHazards: Int,
    val reports: List<PhotoAnalysisReport>
)

data class ChangeDetectionReport(
    val hasChanges: Boolean,
    val changeAreas: Int,
    val severity: DetectedSeverity,
    val description: String,
    val requiresAttention: Boolean
)
