package com.zama.safeops.frontend.domain.usecase.inspection

import com.zama.safeops.frontend.data.repository.InspectionRepository
import com.zama.safeops.frontend.domain.model.Inspection

class GetInspectionsUseCase(private val repository: InspectionRepository) {
    suspend operator fun invoke(status: String? = null): Result<List<Inspection>> {
        return repository.getInspections(status)
    }
}
