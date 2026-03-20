package com.zama.safeops.frontend.domain.usecase.hazard

import com.zama.safeops.frontend.data.repository.HazardRepository
import com.zama.safeops.frontend.domain.model.Hazard

class GetHazardsUseCase(private val repository: HazardRepository) {
    suspend operator fun invoke(status: String? = null): Result<List<Hazard>> {
        return repository.getHazards(status)
    }
}
