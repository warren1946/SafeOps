package org.example.project.domain.usecase

import org.example.project.domain.model.Result
import org.example.project.domain.repository.AuthRepository

/**
 * Logout Use Case
 */
class LogoutUseCase(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(): Result<Unit> {
        return authRepository.logout()
    }
}
