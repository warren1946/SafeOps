package com.zama.safeops.frontend.data.repository

import com.zama.safeops.frontend.data.api.AuthResponse
import com.zama.safeops.frontend.data.api.SafeOpsApi
import com.zama.safeops.frontend.domain.model.User

class AuthRepository(private val api: SafeOpsApi) {

    suspend fun login(email: String, password: String): Result<User> {
        return api.login(email, password).map { response ->
            api.setAuthToken(response.tokens.accessToken)
            response.user
        }
    }

    suspend fun getCurrentUser(): Result<User> {
        return api.getCurrentUser()
    }

    fun logout() {
        api.setAuthToken("")
        // TODO: Clear local storage
    }
}
