package org.example.project.domain.usecase

import org.example.project.domain.model.Hazard
import org.example.project.domain.model.HazardSeverity
import org.example.project.domain.model.HazardStatus
import org.example.project.domain.model.Result
import org.example.project.domain.repository.HazardRepository

/**
 * Get Hazards Use Case
 */
class GetHazardsUseCase(
    private val hazardRepository: HazardRepository
) {
    suspend operator fun invoke(
        status: HazardStatus? = null,
        severity: HazardSeverity? = null,
        page: Int = 1,
        pageSize: Int = 20
    ): Result<List<Hazard>> {
        return hazardRepository.getHazards(
            status = status?.name,
            severity = severity?.name,
            page = page,
            pageSize = pageSize
        )
    }
}
