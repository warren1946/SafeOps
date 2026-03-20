package com.zama.safeops.frontend.data.repository

import com.zama.safeops.frontend.data.api.SafeOpsApi
import com.zama.safeops.frontend.domain.model.Inspection

class InspectionRepository(private val api: SafeOpsApi) {

    suspend fun getInspections(
        status: String? = null,
        page: Int = 0
    ): Result<List<Inspection>> {
        return api.getInspections(status, page).map { it.content }
    }

    suspend fun getInspectionById(id: Long): Result<Inspection> {
        return api.getInspectionById(id)
    }
}
