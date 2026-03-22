package org.example.project.domain.repository

import kotlinx.coroutines.flow.Flow
import org.example.project.domain.model.AuthTokens
import org.example.project.domain.model.Result
import org.example.project.domain.model.User
import org.example.project.domain.model.UserSession

/**
 * Auth Repository Interface (Repository Pattern)
 * Abstracts the data source from the domain layer
 */
interface AuthRepository {
    /**
     * Login with email and password
     */
    suspend fun login(email: String, password: String): Result<UserSession>
    
    /**
     * Register a new user
     */
    suspend fun register(email: String, password: String, roles: List<String>): Result<User>
    
    /**
     * Logout and clear session
     */
    suspend fun logout(): Result<Unit>
    
    /**
     * Refresh access token
     */
    suspend fun refreshToken(): Result<AuthTokens>
    
    /**
     * Get current user session
     */
    fun getCurrentSession(): Flow<UserSession?>
    
    /**
     * Check if user is logged in
     */
    suspend fun isLoggedIn(): Boolean
    
    /**
     * Get current access token
     */
    fun getAccessToken(): String?
    
    /**
     * Get current refresh token
     */
    fun getRefreshToken(): String?
}
