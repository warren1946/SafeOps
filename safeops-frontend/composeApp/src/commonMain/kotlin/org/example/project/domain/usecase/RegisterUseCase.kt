package org.example.project.domain.usecase

import org.example.project.domain.model.Result
import org.example.project.domain.model.User
import org.example.project.domain.repository.AuthRepository

/**
 * Register Use Case
 */
class RegisterUseCase(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(
        email: String, 
        password: String,
        confirmPassword: String,
        roles: List<String> = listOf("OFFICER")
    ): Result<User> {
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
        if (password != confirmPassword) {
            return Result.Error("Passwords do not match")
        }
        
        // Perform registration
        return authRepository.register(email, password, roles)
    }
}
