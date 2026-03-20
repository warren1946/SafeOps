package com.zama.safeops.frontend.domain.usecase.auth

import com.zama.safeops.frontend.data.repository.AuthRepository
import com.zama.safeops.frontend.domain.model.User

class LoginUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(email: String, password: String): Result<User> {
        if (email.isBlank()) {
            return Result.failure(IllegalArgumentException("Email is required"))
        }
        if (password.isBlank()) {
            return Result.failure(IllegalArgumentException("Password is required"))
        }
        return repository.login(email, password)
    }
}
