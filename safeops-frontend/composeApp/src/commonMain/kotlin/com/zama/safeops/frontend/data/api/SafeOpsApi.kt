package com.zama.safeops.frontend.data.api

import com.zama.safeops.frontend.domain.model.AuthTokens
import com.zama.safeops.frontend.domain.model.Hazard
import com.zama.safeops.frontend.domain.model.Inspection
import com.zama.safeops.frontend.domain.model.SafetyScore
import com.zama.safeops.frontend.domain.model.User
import io.github.aakira.napier.Napier
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import kotlinx.serialization.Serializable

class SafeOpsApi(private val client: HttpClient) {

    private var accessToken: String? = null

    fun setAuthToken(token: String) {
        this.accessToken = token
    }

    private suspend inline fun <reified T> safeApiCall(
        crossinline block: suspend () -> T
    ): Result<T> = try {
        Result.success(block())
    } catch (e: ClientRequestException) {
        Napier.e("Client error: ${e.response.status}", e)
        Result.failure(e)
    } catch (e: ServerResponseException) {
        Napier.e("Server error: ${e.response.status}", e)
        Result.failure(e)
    } catch (e: Exception) {
        Napier.e("Unknown error", e)
        Result.failure(e)
    }

    // Auth
    suspend fun login(email: String, password: String): Result<AuthResponse> = safeApiCall {
        client.post("$BASE_URL/auth/login") {
            setBody(LoginRequest(email, password))
        }.body()
    }

    suspend fun refreshToken(refreshToken: String): Result<AuthTokens> = safeApiCall {
        client.post("$BASE_URL/auth/refresh") {
            setBody(RefreshTokenRequest(refreshToken))
        }.body()
    }

    suspend fun getCurrentUser(): Result<User> = safeApiCall {
        client.get("$BASE_URL/auth/me") {
            accessToken?.let { header("Authorization", "Bearer $it") }
        }.body()
    }

    // Inspections
    suspend fun getInspections(
        status: String? = null,
        page: Int = 0,
        size: Int = 20
    ): Result<PaginatedResponse<Inspection>> = safeApiCall {
        client.get("$BASE_URL/inspections") {
            accessToken?.let { header("Authorization", "Bearer $it") }
            status?.let { parameter("status", it) }
            parameter("page", page)
            parameter("size", size)
        }.body()
    }

    suspend fun getInspectionById(id: Long): Result<Inspection> = safeApiCall {
        client.get("$BASE_URL/inspections/$id") {
            accessToken?.let { header("Authorization", "Bearer $it") }
        }.body()
    }

    // Hazards
    suspend fun getHazards(
        status: String? = null,
        severity: String? = null,
        page: Int = 0,
        size: Int = 20
    ): Result<PaginatedResponse<Hazard>> = safeApiCall {
        client.get("$BASE_URL/hazards") {
            accessToken?.let { header("Authorization", "Bearer $it") }
            status?.let { parameter("status", it) }
            severity?.let { parameter("severity", it) }
            parameter("page", page)
            parameter("size", size)
        }.body()
    }

    suspend fun createHazard(request: CreateHazardRequest): Result<Hazard> = safeApiCall {
        client.post("$BASE_URL/hazards") {
            accessToken?.let { header("Authorization", "Bearer $it") }
            setBody(request)
        }.body()
    }

    // Safety Score
    suspend fun getSafetyScore(): Result<SafetyScore> = safeApiCall {
        client.get("$BASE_URL/analytics/safety-score") {
            accessToken?.let { header("Authorization", "Bearer $it") }
        }.body()
    }

    companion object {
        private const val BASE_URL = "http://localhost:8080/api/v1"
    }
}

// Request/Response DTOs
@Serializable
data class LoginRequest(val email: String, val password: String)

@Serializable
data class RefreshTokenRequest(val refreshToken: String)

@Serializable
data class AuthResponse(
    val user: User,
    val tokens: AuthTokens
)

@Serializable
data class CreateHazardRequest(
    val title: String,
    val description: String? = null,
    val category: String,
    val severity: String,
    val location: String? = null
)

@Serializable
data class PaginatedResponse<T>(
    val content: List<T>,
    val totalElements: Long,
    val totalPages: Int,
    val number: Int,
    val size: Int,
    val first: Boolean,
    val last: Boolean
)
