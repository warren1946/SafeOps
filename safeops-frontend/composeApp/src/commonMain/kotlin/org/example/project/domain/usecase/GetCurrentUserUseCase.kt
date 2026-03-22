package org.example.project.domain.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.example.project.domain.model.Result
import org.example.project.domain.model.User
import org.example.project.domain.repository.AuthRepository

/**
 * Get Current User Use Case
 */
class GetCurrentUserUseCase(
    private val authRepository: AuthRepository
) {
    operator fun invoke(): Flow<Result<User?>> {
        return authRepository.getCurrentSession().map { session ->
            session?.let { Result.Success(it.user) } ?: Result.Success(null)
        }
    }
    
    suspend fun isLoggedIn(): Boolean {
        return authRepository.isLoggedIn()
    }
}
