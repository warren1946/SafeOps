package org.example.project.domain.repository

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
        status: String? = null,
        severity: String? = null,
        page: Int = 1,
        pageSize: Int = 20
    ): Result<List<Hazard>>
    
    /**
     * Get hazard by ID
     */
    suspend fun getHazardById(id: Long): Result<Hazard>
    
    /**
     * Create a new hazard
     */
    suspend fun createHazard(
        title: String,
        description: String? = null,
        location: String? = null,
        severity: HazardSeverity = HazardSeverity.MEDIUM
    ): Result<Hazard>
    
    /**
     * Update an existing hazard
     */
    suspend fun updateHazard(
        id: Long,
        title: String? = null,
        description: String? = null,
        location: String? = null,
        severity: HazardSeverity? = null,
        status: HazardStatus? = null
    ): Result<Hazard>
    
    /**
     * Delete a hazard
     */
    suspend fun deleteHazard(id: Long): Result<Unit>
}
