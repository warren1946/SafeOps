/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 */

package com.zama.safeops.modules.ai.domain.model

import java.time.Instant

/**
 * AI Analysis result for an image.
 */
data class AIImageAnalysis(
    val id: String,
    val photoUrl: String,
    val analysisType: AnalysisType,
    val hazardsDetected: List<DetectedHazard>,
    val overallRiskScore: Double,
    val confidence: Double,
    val processingTimeMs: Long,
    val rawResults: Map<String, Any>?,
    val createdAt: Instant = Instant.now()
)

enum class AnalysisType {
    HAZARD_DETECTION,
    PPE_COMPLIANCE,
    CHANGE_DETECTION,
    GENERAL_CLASSIFICATION
}

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
