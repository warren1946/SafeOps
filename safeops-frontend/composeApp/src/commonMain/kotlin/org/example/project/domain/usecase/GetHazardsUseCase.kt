package org.example.project.domain.usecase

import kotlinx.coroutines.flow.Flow
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
        severity: HazardSeverity? = null
    ): Result<List<Hazard>> {
        return hazardRepository.getHazards(status, severity)
    }
    
    fun asFlow(
        status: HazardStatus? = null,
        severity: HazardSeverity? = null
    ): Flow<Result<List<Hazard>>> {
        return hazardRepository.getHazardsFlow(status, severity)
    }
}
