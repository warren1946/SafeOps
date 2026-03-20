package com.zama.safeops.frontend.domain.usecase.safety

import com.zama.safeops.frontend.data.repository.SafetyScoreRepository
import com.zama.safeops.frontend.domain.model.SafetyScore

class GetSafetyScoreUseCase(private val repository: SafetyScoreRepository) {
    suspend operator fun invoke(): Result<SafetyScore> {
        return repository.getSafetyScore()
    }
}
