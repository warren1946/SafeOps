package com.zama.safeops.frontend.data.api

import com.zama.safeops.frontend.domain.model.*
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
        crossinline block: suspend () -> ApiResponse<T>
    ): Result<T> = try {
        val response = block()
        if (response.success && response.data != null) {
            Result.success(response.data)
        } else {
            Result.failure(ApiException(response.message))
        }
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
    suspend fun login(email: String, password: String): Result<User> = safeApiCall {
        val response: ApiResponse<LoginData> = client.post("$BASE_URL/auth/login") {
            setBody(LoginRequest(email, password))
        }.body()

        // Store token from login response
        response.data?.accessToken?.let { setAuthToken(it) }

        // Map LoginData to User
        ApiResponse(
            success = response.success,
            message = response.message,
            data = response.data?.toUser()
        )
    }

    suspend fun refreshToken(refreshToken: String): Result<AuthTokens> = safeApiCall {
        client.post("$BASE_URL/auth/refresh") {
            setBody(RefreshTokenRequest(refreshToken))
        }.body()
    }

    suspend fun getCurrentUser(): Result<User> = safeApiCall {
        client.get("$BASE_URL/auth/users/me") {
            accessToken?.let { header("Authorization", "Bearer $it") }
        }.body()
    }

    // Users (Admin only)
    suspend fun getUsers(): Result<List<User>> = safeApiCall {
        client.get("$BASE_URL/auth/users") {
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
            header("X-Tenant-ID", "1")
            header("X-Tenant-Slug", "default")
            status?.let { parameter("status", it) }
            parameter("page", page)
            parameter("size", size)
        }.body()
    }

    suspend fun getInspectionById(id: Long): Result<Inspection> = safeApiCall {
        client.get("$BASE_URL/inspections/$id") {
            accessToken?.let { header("Authorization", "Bearer $it") }
            header("X-Tenant-ID", "1")
            header("X-Tenant-Slug", "default")
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
            header("X-Tenant-ID", "1")
            header("X-Tenant-Slug", "default")
            status?.let { parameter("status", it) }
            severity?.let { parameter("severity", it) }
            parameter("page", page)
            parameter("size", size)
        }.body()
    }

    suspend fun createHazard(request: CreateHazardRequest): Result<Hazard> = safeApiCall {
        client.post("$BASE_URL/hazards") {
            accessToken?.let { header("Authorization", "Bearer $it") }
            header("X-Tenant-ID", "1")
            header("X-Tenant-Slug", "default")
            setBody(request)
        }.body()
    }

    // Safety Score
    suspend fun getSafetyScore(): Result<SafetyScore> = safeApiCall {
        client.get("$BASE_URL/dashboard/stats") {
            accessToken?.let { header("Authorization", "Bearer $it") }
            header("X-Tenant-ID", "1")
            header("X-Tenant-Slug", "default")
        }.body()
    }

    // Tenants (Admin/Super Admin only)
    suspend fun getTenants(): Result<List<Tenant>> = safeApiCall {
        client.get("$BASE_URL/admin/tenants") {
            accessToken?.let { header("Authorization", "Bearer $it") }
            header("X-Tenant-ID", "1")
            header("X-Tenant-Slug", "default")
        }.body()
    }

    companion object {
        private const val BASE_URL = "https://safeops-1.onrender.com/api"
    }
}

class ApiException(message: String) : Exception(message)

// Generic API Response wrapper
@Serializable
data class ApiResponse<T>(
    val success: Boolean,
    val message: String,
    val data: T? = null,
    val code: String? = null
)

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

// Helper function to map LoginData to User
private fun LoginData.toUser(): User {
    return User(
        id = this.id,
        email = this.email,
        roles = this.roles.map { roleName ->
            try {
                UserRole.valueOf(roleName)
            } catch (e: IllegalArgumentException) {
                UserRole.OFFICER // Default fallback
            }
        },
        enabled = this.enabled
    )
}

// Tenant DTO
@Serializable
data class Tenant(
    val id: Long,
    val slug: String,
    val name: String,
    val status: String,
    val plan: String
)
