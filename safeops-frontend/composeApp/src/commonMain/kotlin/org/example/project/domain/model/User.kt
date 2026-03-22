package org.example.project.domain.model

/**
 * Domain model for User (Clean Architecture - Domain Layer)
 * This is independent of any external frameworks or data sources
 */
data class User(
    val id: Long,
    val email: String,
    val roles: List<UserRole>,
    val tenantId: Long? = null,
    val isActive: Boolean = true
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
