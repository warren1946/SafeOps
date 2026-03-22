package org.example.project.domain.repository

import kotlinx.coroutines.flow.Flow
import org.example.project.domain.model.Result
import org.example.project.domain.model.User

/**
 * User Repository Interface
 */
interface UserRepository {
    /**
     * Get all users
     */
    suspend fun getUsers(): Result<List<User>>
    
    /**
     * Get users as Flow for reactive updates
     */
    fun getUsersFlow(): Flow<Result<List<User>>>
    
    /**
     * Create a new user
     */
    suspend fun createUser(
        email: String,
        password: String,
        roles: List<String>
    ): Result<User>
    
    /**
     * Get user by ID
     */
    suspend fun getUserById(id: Long): Result<User>
}
