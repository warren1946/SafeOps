package org.example.project.domain.repository

import kotlinx.coroutines.flow.Flow
import org.example.project.domain.model.Hazard
import org.example.project.domain.model.HazardSeverity
import org.example.project.domain.model.HazardStatus
import org.example.project.domain.model.Result

/**
 * Hazard Repository Interface
 */
interface HazardRepository {
    /**
     * Get all hazards with optional filters
     */
    suspend fun getHazards(
        status: HazardStatus? = null,
        severity: HazardSeverity? = null
    ): Result<List<Hazard>>
    
    /**
     * Get hazards as Flow for reactive updates
     */
    fun getHazardsFlow(
        status: HazardStatus? = null,
        severity: HazardSeverity? = null
    ): Flow<Result<List<Hazard>>>
    
    /**
     * Create a new hazard
     */
    suspend fun createHazard(
        title: String,
        description: String? = null,
        severity: HazardSeverity = HazardSeverity.MEDIUM,
        location: String? = null,
        targetType: String? = null,
        targetId: Long? = null
    ): Result<Hazard>
    
    /**
     * Get critical hazards count
     */
    suspend fun getCriticalHazardsCount(): Result<Int>
    
    /**
     * Get open hazards count
     */
    suspend fun getOpenHazardsCount(): Result<Int>
}
