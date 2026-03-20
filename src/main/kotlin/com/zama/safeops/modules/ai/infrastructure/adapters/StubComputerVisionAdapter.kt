/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 */

package com.zama.safeops.modules.ai.infrastructure.adapters

import com.zama.safeops.modules.ai.application.ports.*
import com.zama.safeops.modules.ai.domain.model.DetectedSeverity
import org.springframework.stereotype.Component

/**
 * Stub implementation of ComputerVisionPort.
 * Returns empty results - AI features are disabled until a real implementation is configured.
 */
@Component
class StubComputerVisionAdapter : ComputerVisionPort {

    override fun analyzeForHazards(request: HazardAnalysisRequest): HazardAnalysisResult {
        return HazardAnalysisResult(
            hazardsDetected = emptyList(),
            overallRiskScore = 0.0,
            confidence = 1.0,
            processingTimeMs = 0
        )
    }

    override fun detectPPE(imageUrl: String): PPEComplianceResult {
        return PPEComplianceResult(
            personsDetected = 0,
            compliantPersons = 0,
            violations = emptyList(),
            complianceRate = 1.0,
            confidence = 1.0
        )
    }

    override fun classifyImage(imageUrl: String): ImageClassification {
        return ImageClassification(
            category = ImageCategory.UNKNOWN,
            confidence = 1.0,
            tags = emptyList()
        )
    }

    override fun detectChanges(beforeImage: String, afterImage: String): ChangeDetectionResult {
        return ChangeDetectionResult(
            changesDetected = false,
            changeAreas = emptyList(),
            severity = DetectedSeverity.LOW,
            confidence = 1.0
        )
    }
}
