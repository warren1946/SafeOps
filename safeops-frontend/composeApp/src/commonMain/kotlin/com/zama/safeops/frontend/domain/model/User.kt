package com.zama.safeops.frontend.domain.model

import kotlinx.serialization.Serializable

/**
 * User roles matching the backend authentication system.
 * Each role has specific permissions and access levels.
 */
@Serializable
enum class UserRole {
    /**
     * Platform administrator - full system access.
     * Can manage tenants, users, and system configuration.
     */
    SUPER_ADMIN,

    /**
     * Tenant administrator - manages a specific tenant/organization.
     * Can manage users, locations, and view all reports.
     */
    ADMIN,

    /**
     * Mine supervisor - oversees operations at a specific site.
     * Can create inspections, view reports, manage hazards.
     */
    SUPERVISOR,

    /**
     * Field safety officer - conducts inspections and reports hazards.
     * Mobile-focused role with offline capabilities.
     */
    OFFICER,

    /**
     * Read-only access for viewing reports and dashboards.
     * Cannot create or modify data.
     */
    VIEWER
}

/**
 * Extension functions for role-based access control
 */
fun UserRole.canManageUsers(): Boolean = this in setOf(UserRole.SUPER_ADMIN, UserRole.ADMIN)
fun UserRole.canManageTenants(): Boolean = this == UserRole.SUPER_ADMIN
fun UserRole.canCreateInspections(): Boolean = this in setOf(UserRole.SUPER_ADMIN, UserRole.ADMIN, UserRole.SUPERVISOR, UserRole.OFFICER)
fun UserRole.canApproveInspections(): Boolean = this in setOf(UserRole.SUPER_ADMIN, UserRole.ADMIN, UserRole.SUPERVISOR)
fun UserRole.canManageHazards(): Boolean = this in setOf(UserRole.SUPER_ADMIN, UserRole.ADMIN, UserRole.SUPERVISOR, UserRole.OFFICER)
fun UserRole.canViewReports(): Boolean = this in UserRole.entries.toTypedArray()
fun UserRole.canManageEquipment(): Boolean = this in setOf(UserRole.SUPER_ADMIN, UserRole.ADMIN, UserRole.SUPERVISOR)
fun UserRole.canAccessAdminPanel(): Boolean = this in setOf(UserRole.SUPER_ADMIN, UserRole.ADMIN)
fun UserRole.canDeleteData(): Boolean = this in setOf(UserRole.SUPER_ADMIN, UserRole.ADMIN)

@Serializable
data class User(
    val id: Long,
    val email: String,
    val firstName: String? = null,
    val lastName: String? = null,
    val roles: List<UserRole> = emptyList(),
    val tenantId: Long? = null,
    val tenantName: String? = null,
    val mineId: Long? = null,
    val mineName: String? = null,
    val profileImageUrl: String? = null,
    val enabled: Boolean = true,
    val createdAt: String? = null
) {
    /**
     * Get the primary role (highest permission level)
     */
    val primaryRole: UserRole
        get() = roles.firstOrNull() ?: UserRole.VIEWER

    /**
     * Check if user has a specific role
     */
    fun hasRole(role: UserRole): Boolean = roles.contains(role)

    /**
     * Check if user has any of the specified roles
     */
    fun hasAnyRole(vararg rolesToCheck: UserRole): Boolean = roles.any { it in rolesToCheck }

    /**
     * Display name for the user
     */
    val displayName: String
        get() = if (firstName != null && lastName != null) {
            "$firstName $lastName"
        } else {
            email
        }

    companion object {
        /**
         * Create a guest user for unauthenticated state
         */
        fun guest(): User = User(
            id = 0,
            email = "",
            roles = listOf(UserRole.VIEWER),
            enabled = false
        )
    }
}

@Serializable
data class AuthTokens(
    val accessToken: String,
    val refreshToken: String,
    val expiresIn: Long
)

/**
 * Login response from the backend
 */
@Serializable
data class LoginResponse(
    val success: Boolean,
    val message: String,
    val data: LoginData? = null
)

@Serializable
data class LoginData(
    val id: Long,
    val email: String,
    val roles: List<String>,
    val enabled: Boolean,
    val accessToken: String,
    val refreshToken: String
)

/**
 * Registration request
 */
@Serializable
data class RegisterRequest(
    val email: String,
    val password: String,
    val roles: List<String> = listOf("OFFICER")
)

/**
 * User list response
 */
@Serializable
data class UsersResponse(
    val success: Boolean,
    val message: String,
    val data: List<UserDto>? = null
)

@Serializable
data class UserDto(
    val id: Long,
    val email: String,
    val roles: List<String>,
    val enabled: Boolean
)
