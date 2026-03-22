package org.example.project.domain.repository

import kotlinx.coroutines.flow.Flow
import org.example.project.domain.model.DashboardChartData
import org.example.project.domain.model.DashboardStatistics
import org.example.project.domain.model.Result

/**
 * Dashboard Repository Interface
 */
interface DashboardRepository {
    /**
     * Get dashboard statistics
     */
    suspend fun getDashboardStatistics(): Result<DashboardStatistics>
    
    /**
     * Get dashboard statistics as Flow for reactive updates
     */
    fun getDashboardStatisticsFlow(): Flow<Result<DashboardStatistics>>
    
    /**
     * Get chart data for dashboard
     */
    suspend fun getChartData(): Result<DashboardChartData>
}
