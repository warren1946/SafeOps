package org.example.project.domain.model

/**
 * Domain model for User (Clean Architecture - Domain Layer)
 * This is independent of any external frameworks or data sources
 */
data class User(
    val id: Long,
    val email: String,
    val firstName: String? = null,
    val lastName: String? = null,
    val phone: String? = null,
    val avatarUrl: String? = null,
    val roles: List<UserRole> = emptyList(),
    val role: String? = null, // For backward compatibility
    val tenantId: Long? = null,
    val isActive: Boolean = true,
    val emailVerified: Boolean = false
)

enum class UserRole {
    ADMIN, SUPERVISOR, OFFICER;
    
    companion object {
        fun fromString(role: String): UserRole = when (role.uppercase()) {
            "ADMIN" -> ADMIN
            "SUPERVISOR" -> SUPERVISOR
            "OFFICER" -> OFFICER
            else -> OFFICER
        }
    }
}

/**
 * Authentication tokens domain model
 */
data class AuthTokens(
    val accessToken: String,
    val refreshToken: String,
    val expiresIn: Long = 900 // 15 minutes default
)

/**
 * Authenticated user session
 */
data class UserSession(
    val user: User,
    val tokens: AuthTokens
)
