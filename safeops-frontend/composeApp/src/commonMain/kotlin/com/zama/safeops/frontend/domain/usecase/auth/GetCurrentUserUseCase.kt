package com.zama.safeops.frontend.domain.usecase.auth

import com.zama.safeops.frontend.data.repository.AuthRepository
import com.zama.safeops.frontend.domain.model.User

class GetCurrentUserUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(): Result<User> {
        return repository.getCurrentUser()
    }
}
