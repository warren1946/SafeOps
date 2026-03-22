package org.example.project.domain.usecase

import kotlinx.coroutines.flow.Flow
import org.example.project.domain.model.Inspection
import org.example.project.domain.model.Result
import org.example.project.domain.repository.InspectionRepository

/**
 * Get Inspections Use Case
 */
class GetInspectionsUseCase(
    private val inspectionRepository: InspectionRepository
) {
    suspend operator fun invoke(): Result<List<Inspection>> {
        return inspectionRepository.getInspections()
    }
    
    fun asFlow(): Flow<Result<List<Inspection>>> {
        return inspectionRepository.getInspectionsFlow()
    }
}
