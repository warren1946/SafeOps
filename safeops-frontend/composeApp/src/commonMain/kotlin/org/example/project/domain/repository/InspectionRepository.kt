package org.example.project.domain.repository

import kotlinx.coroutines.flow.Flow
import org.example.project.domain.model.Inspection
import org.example.project.domain.model.Result

/**
 * Inspection Repository Interface
 */
interface InspectionRepository {
    /**
     * Get all inspections
     */
    suspend fun getInspections(): Result<List<Inspection>>
    
    /**
     * Get inspections as Flow for reactive updates
     */
    fun getInspectionsFlow(): Flow<Result<List<Inspection>>>
    
    /**
     * Get a single inspection by ID
     */
    suspend fun getInspection(id: Long): Result<Inspection>
    
    /**
     * Create a new inspection
     */
    suspend fun createInspection(
        title: String,
        targetType: String,
        targetId: Long,
        templateId: Long? = null,
        assignedOfficerId: Long? = null
    ): Result<Inspection>
    
    /**
     * Submit an inspection
     */
    suspend fun submitInspection(
        inspectionId: Long,
        responses: List<Any>,
        notes: String? = null
    ): Result<Unit>
}
