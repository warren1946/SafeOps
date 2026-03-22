package org.example.project.domain.usecase

import org.example.project.domain.model.Result
import org.example.project.domain.model.UserSession
import org.example.project.domain.repository.AuthRepository

/**
 * Login Use Case (Single Responsibility Principle)
 * Encapsulates the login business logic
 */
class LoginUseCase(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String): Result<UserSession> {
        // Validate input
        if (email.isBlank()) {
            return Result.Error("Email is required")
        }
        if (!email.contains("@")) {
            return Result.Error("Invalid email format")
        }
        if (password.isBlank()) {
            return Result.Error("Password is required")
        }
        if (password.length < 6) {
            return Result.Error("Password must be at least 6 characters")
        }
        
        // Perform login
        return authRepository.login(email, password)
    }
}
