package com.zama.safeops.frontend.data.repository

import com.zama.safeops.frontend.data.api.SafeOpsApi
import com.zama.safeops.frontend.domain.model.SafetyScore

class SafetyScoreRepository(private val api: SafeOpsApi) {

    suspend fun getSafetyScore(): Result<SafetyScore> {
        return api.getSafetyScore()
    }
}
