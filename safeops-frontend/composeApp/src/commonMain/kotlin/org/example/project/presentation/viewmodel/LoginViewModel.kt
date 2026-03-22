package org.example.project.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.example.project.domain.model.Result
import org.example.project.domain.model.UserSession
import org.example.project.domain.usecase.LoginUseCase

/**
 * Login ViewModel (MVVM Pattern)
 * Manages UI state and business logic for Login screen
 */
class LoginViewModel(
    private val loginUseCase: LoginUseCase
) : ViewModel() {
    
    // UI State
    data class LoginUiState(
        val email: String = "",
        val password: String = "",
        val isLoading: Boolean = false,
        val error: String? = null,
        val isSuccess: Boolean = false,
        val userSession: UserSession? = null
    )
    
    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()
    
    // Email validation
    val isEmailValid: Boolean
        get() = _uiState.value.email.contains("@") && _uiState.value.email.contains(".")
    
    // Password validation
    val isPasswordValid: Boolean
        get() = _uiState.value.password.length >= 6
    
    // Form validation
    val isFormValid: Boolean
        get() = isEmailValid && isPasswordValid
    
    fun onEmailChange(email: String) {
        _uiState.value = _uiState.value.copy(
            email = email,
            error = null
        )
    }
    
    fun onPasswordChange(password: String) {
        _uiState.value = _uiState.value.copy(
            password = password,
            error = null
        )
    }
    
    fun login() {
        val currentState = _uiState.value
        
        if (!isFormValid) {
            _uiState.value = currentState.copy(
                error = when {
                    currentState.email.isBlank() -> "Email is required"
                    !isEmailValid -> "Invalid email format"
                    currentState.password.isBlank() -> "Password is required"
                    !isPasswordValid -> "Password must be at least 6 characters"
                    else -> "Please check your input"
                }
            )
            return
        }
        
        viewModelScope.launch {
            _uiState.value = currentState.copy(isLoading = true, error = null)
            
            when (val result = loginUseCase(currentState.email, currentState.password)) {
                is Result.Success -> {
                    println("LoginViewModel: Login successful for ${result.data.user.email}")
                    _uiState.value = LoginUiState(
                        isSuccess = true,
                        userSession = result.data
                    )
                }
                is Result.Error -> {
                    println("LoginViewModel: Login failed - ${result.message}")
                    _uiState.value = currentState.copy(
                        isLoading = false,
                        error = result.message
                    )
                }
                is Result.Loading -> {
                    _uiState.value = currentState.copy(isLoading = true)
                }
            }
        }
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
    
    fun resetState() {
        _uiState.value = LoginUiState()
    }
}
