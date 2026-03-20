package com.zama.safeops.frontend.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: Long,
    val email: String,
    val firstName: String,
    val lastName: String,
    val role: UserRole,
    val tenantId: Long,
    val tenantName: String? = null,
    val department: String? = null,
    val profileImageUrl: String? = null,
    val isActive: Boolean = true
)

@Serializable
enum class UserRole {
    ADMIN,
    SUPERVISOR,
    INSPECTOR,
    WORKER,
    SAFETY_OFFICER,
    GUEST
}

@Serializable
data class AuthTokens(
    val accessToken: String,
    val refreshToken: String,
    val expiresIn: Long
)
