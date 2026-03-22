package org.example.project.domain.usecase

import kotlinx.coroutines.flow.Flow
import org.example.project.domain.model.Result
import org.example.project.domain.model.User
import org.example.project.domain.repository.UserRepository

/**
 * Get Users Use Case
 */
class GetUsersUseCase(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(): Result<List<User>> {
        return userRepository.getUsers()
    }
    
    fun asFlow(): Flow<Result<List<User>>> {
        return userRepository.getUsersFlow()
    }
}
