package org.example.project.domain.usecase

import kotlinx.coroutines.flow.Flow
import org.example.project.domain.model.DashboardStatistics
import org.example.project.domain.model.Result
import org.example.project.domain.repository.DashboardRepository

/**
 * Get Dashboard Statistics Use Case
 */
class GetDashboardStatisticsUseCase(
    private val dashboardRepository: DashboardRepository
) {
    suspend operator fun invoke(): Result<DashboardStatistics> {
        return dashboardRepository.getDashboardStatistics()
    }
    
    fun asFlow(): Flow<Result<DashboardStatistics>> {
        return dashboardRepository.getDashboardStatisticsFlow()
    }
}
