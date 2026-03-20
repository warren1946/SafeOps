package com.zama.safeops.frontend.domain.usecase.auth

import com.zama.safeops.frontend.data.repository.AuthRepository

class LogoutUseCase(private val repository: AuthRepository) {
    operator fun invoke() {
        repository.logout()
    }
}
