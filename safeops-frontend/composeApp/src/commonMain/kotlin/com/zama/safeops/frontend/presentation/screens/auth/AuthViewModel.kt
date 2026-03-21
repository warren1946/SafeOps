package com.zama.safeops.frontend.presentation.screens.auth

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.zama.safeops.frontend.domain.model.User
import com.zama.safeops.frontend.domain.usecase.auth.GetCurrentUserUseCase
import com.zama.safeops.frontend.domain.usecase.auth.LoginUseCase
import com.zama.safeops.frontend.domain.usecase.auth.LogoutUseCase
import com.zama.safeops.frontend.presentation.rbac.UserSession
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel(
    private val loginUseCase: LoginUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase
) : ScreenModel {

    private val _state = MutableStateFlow<AuthState>(AuthState.Idle)
    val state: StateFlow<AuthState> = _state.asStateFlow()

    fun login(email: String, password: String) {
        screenModelScope.launch {
            _state.value = AuthState.Loading

            loginUseCase(email, password)
                .onSuccess { user ->
                    // Store user in global session for RBAC
                    UserSession.login(user)
                    _state.value = AuthState.Authenticated(user)
                }
                .onFailure { error ->
                    _state.value = AuthState.Error(error.message ?: "Login failed")
                }
        }
    }

    fun checkAuthStatus() {
        screenModelScope.launch {
            _state.value = AuthState.Loading

            getCurrentUserUseCase()
                .onSuccess { user ->
                    // Store user in global session for RBAC
                    UserSession.login(user)
                    _state.value = AuthState.Authenticated(user)
                }
                .onFailure {
                    _state.value = AuthState.Idle
                }
        }
    }

    fun logout() {
        logoutUseCase()
        UserSession.logout()
        _state.value = AuthState.Idle
    }
}

sealed class AuthState {
    data object Idle : AuthState()
    data object Loading : AuthState()
    data class Authenticated(val user: User) : AuthState()
    data class Error(val message: String) : AuthState()
}
