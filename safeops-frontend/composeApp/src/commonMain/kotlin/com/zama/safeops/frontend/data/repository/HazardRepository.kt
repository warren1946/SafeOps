package com.zama.safeops.frontend.data.repository

import com.zama.safeops.frontend.data.api.CreateHazardRequest
import com.zama.safeops.frontend.data.api.SafeOpsApi
import com.zama.safeops.frontend.domain.model.Hazard

class HazardRepository(private val api: SafeOpsApi) {

    suspend fun getHazards(
        status: String? = null,
        severity: String? = null,
        page: Int = 0
    ): Result<List<Hazard>> {
        return api.getHazards(status, severity, page).map { it.content }
    }

    suspend fun createHazard(
        title: String,
        description: String?,
        category: String,
        severity: String,
        location: String?
    ): Result<Hazard> {
        val request = CreateHazardRequest(title, description, category, severity, location)
        return api.createHazard(request)
    }
}
