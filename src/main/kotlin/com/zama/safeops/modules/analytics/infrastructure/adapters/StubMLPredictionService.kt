/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 */

package com.zama.safeops.modules.analytics.infrastructure.adapters

import com.zama.safeops.modules.analytics.application.ports.MLPredictionService
import com.zama.safeops.modules.analytics.domain.model.PredictiveInsight
import com.zama.safeops.modules.tenant.domain.valueobjects.TenantId
import org.springframework.stereotype.Component

/**
 * Stub implementation of MLPredictionService.
 */
@Component
class StubMLPredictionService : MLPredictionService {
    override fun generateInsights(tenantId: TenantId): List<PredictiveInsight> {
        return emptyList()
    }
}
