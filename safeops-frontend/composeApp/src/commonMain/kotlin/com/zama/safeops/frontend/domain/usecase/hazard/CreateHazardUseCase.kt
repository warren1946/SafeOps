package com.zama.safeops.frontend.domain.usecase.hazard

import com.zama.safeops.frontend.data.repository.HazardRepository
import com.zama.safeops.frontend.domain.model.Hazard

class CreateHazardUseCase(private val repository: HazardRepository) {
    suspend operator fun invoke(
        title: String,
        description: String?,
        category: String,
        severity: String,
        location: String?
    ): Result<Hazard> {
        if (title.isBlank()) {
            return Result.failure(IllegalArgumentException("Title is required"))
        }
        return repository.createHazard(title, description, category, severity, location)
    }
}
