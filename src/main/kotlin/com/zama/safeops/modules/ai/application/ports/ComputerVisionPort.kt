/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 */

package com.zama.safeops.modules.ai.application.ports

import com.zama.safeops.modules.ai.domain.model.BoundingBox
import com.zama.safeops.modules.ai.domain.model.DetectedHazard
import com.zama.safeops.modules.ai.domain.model.DetectedSeverity
import com.zama.safeops.modules.ai.domain.model.HazardType
import com.zama.safeops.modules.tenant.domain.valueobjects.TenantId

/**
 * Port for AI-powered computer vision analysis.
 * Abstracts the underlying AI service (TensorFlow, OpenAI, AWS Rekognition, etc.)
 */
interface ComputerVisionPort {

    /**
     * Analyze an image for safety hazards.
     */
    fun analyzeForHazards(request: HazardAnalysisRequest): HazardAnalysisResult

    /**
     * Detect PPE compliance in an image.
     */
    fun detectPPE(imageUrl: String): PPEComplianceResult

    /**
     * Classify image type for routing.
     */
    fun classifyImage(imageUrl: String): ImageClassification

    /**
     * Compare before/after images for change detection.
     */
    fun detectChanges(beforeImage: String, afterImage: String): ChangeDetectionResult
}

data class HazardAnalysisRequest(
    val imageUrl: String,
    val locationContext: String?,  // e.g., "underground shaft A level 3"
    val tenantId: TenantId,
    val inspectionType: String? = null
)

data class HazardAnalysisResult(
    val hazardsDetected: List<DetectedHazard>,
    val overallRiskScore: Double,  // 0-100
    val confidence: Double,
    val processingTimeMs: Long,
    val rawAnalysis: Map<String, Any>? = null
)

data class DetectedHazard(
    val hazardType: HazardType,
    val description: String,
    val severity: DetectedSeverity,
    val confidence: Double,
    val boundingBox: BoundingBox?,
    val recommendedAction: String
)

enum class HazardType {
    // Structural
    CRACK_IN_WALL,
    CORROSION,
    LOOSE_ROCK,
    WATER_DAMAGE,
    DAMAGED_SUPPORT,

    // Equipment
    FAULTY_MACHINERY,
    LEAKING_HYDRAULICS,
    WORN_CABLES,
    DAMAGED_GUARD,

    // Environmental
    POOR_VENTILATION,
    DUST_ACCUMULATION,
    POOR_LIGHTING,
    BLOCKED_EXIT,

    // Safety Equipment
    MISSING_FIRE_EXTINGUISHER,
    DAMAGED_FIRST_AID,
    BLOCKED_EYEWASH,

    // Personnel
    NO_PPE,
    IMPROPER_PPE,
    UNSAFE_POSITION,
    FATIGUE_INDICATORS
}

enum class DetectedSeverity {
    LOW, MEDIUM, HIGH, CRITICAL
}

data class BoundingBox(
    val x: Double,
    val y: Double,
    val width: Double,
    val height: Double
)

data class PPEComplianceResult(
    val personsDetected: Int,
    val compliantPersons: Int,
    val violations: List<PPEViolation>,
    val complianceRate: Double,
    val confidence: Double
)

data class PPEViolation(
    val personId: Int,
    val missingItems: List<PPEItem>,
    val improperUse: List<PPEItem>,
    val boundingBox: BoundingBox
)

enum class PPEItem {
    HARD_HAT,
    SAFETY_GLASSES,
    HIGH_VISIBILITY_VEST,
    SAFETY_BOOTS,
    GLOVES,
    HEARING_PROTECTION,
    RESPIRATOR,
    HARNESS
}

data class ImageClassification(
    val category: ImageCategory,
    val confidence: Double,
    val tags: List<String>
)

enum class ImageCategory {
    INSPECTION_PHOTO,
    HAZARD_EVIDENCE,
    PPE_CHECK,
    EQUIPMENT_PHOTO,
    LOCATION_MARKER,
    DOCUMENT,
    UNKNOWN
}

data class ChangeDetectionResult(
    val changesDetected: Boolean,
    val changeAreas: List<ChangeArea>,
    val severity: DetectedSeverity,
    val confidence: Double
)

data class ChangeArea(
    val description: String,
    val boundingBox: BoundingBox,
    val severity: DetectedSeverity
)
