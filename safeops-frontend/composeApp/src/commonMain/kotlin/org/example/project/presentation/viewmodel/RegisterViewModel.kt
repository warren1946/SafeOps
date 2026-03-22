package org.example.project.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.example.project.domain.model.Result
import org.example.project.domain.model.User
import org.example.project.domain.usecase.RegisterUseCase

/**
 * Register ViewModel
 * Manages UI state and business logic for Register screen
 */
class RegisterViewModel(
    private val registerUseCase: RegisterUseCase
) : ViewModel() {
    
    // UI State
    data class RegisterUiState(
        val fullName: String = "",
        val email: String = "",
        val phone: String = "",
        val company: String = "",
        val password: String = "",
        val confirmPassword: String = "",
        val isLoading: Boolean = false,
        val error: String? = null,
        val isSuccess: Boolean = false,
        val createdUser: User? = null
    )
    
    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()
    
    // Full name validation
    val isFullNameValid: Boolean
        get() = _uiState.value.fullName.isNotBlank()
    
    // Email validation - must contain @ and .
    val isEmailValid: Boolean
        get() = _uiState.value.email.contains("@") && _uiState.value.email.contains(".")
    
    // Phone validation
    val isPhoneValid: Boolean
        get() = _uiState.value.phone.isNotBlank()
    
    // Company validation
    val isCompanyValid: Boolean
        get() = _uiState.value.company.isNotBlank()
    
    // Password validation
    val isPasswordValid: Boolean
        get() = _uiState.value.password.length >= 6
    
    // Confirm password validation
    val isConfirmPasswordValid: Boolean
        get() = _uiState.value.confirmPassword == _uiState.value.password && 
                _uiState.value.confirmPassword.isNotBlank()
    
    // Show confirm password field only if password is not empty
    val showConfirmPassword: Boolean
        get() = _uiState.value.password.isNotBlank()
    
    // Form validation
    val isFormValid: Boolean
        get() = isFullNameValid && isEmailValid && isPhoneValid && 
                isCompanyValid && isPasswordValid && isConfirmPasswordValid
    
    fun onFullNameChange(fullName: String) {
        _uiState.value = _uiState.value.copy(
            fullName = fullName,
            error = null
        )
    }
    
    fun onEmailChange(email: String) {
        _uiState.value = _uiState.value.copy(
            email = email,
            error = null
        )
    }
    
    fun onPhoneChange(phone: String) {
        _uiState.value = _uiState.value.copy(
            phone = phone,
            error = null
        )
    }
    
    fun onCompanyChange(company: String) {
        _uiState.value = _uiState.value.copy(
            company = company,
            error = null
        )
    }
    
    fun onPasswordChange(password: String) {
        _uiState.value = _uiState.value.copy(
            password = password,
            error = null
        )
    }
    
    fun onConfirmPasswordChange(confirmPassword: String) {
        _uiState.value = _uiState.value.copy(
            confirmPassword = confirmPassword,
            error = null
        )
    }
    
    fun register() {
        val currentState = _uiState.value
        
        if (!isFormValid) {
            _uiState.value = currentState.copy(
                error = when {
                    currentState.fullName.isBlank() -> "Full name is required"
                    !isEmailValid -> "Please enter a valid email address"
                    currentState.phone.isBlank() -> "Phone number is required"
                    currentState.company.isBlank() -> "Company name is required"
                    currentState.password.isBlank() -> "Password is required"
                    !isPasswordValid -> "Password must be at least 6 characters"
                    currentState.confirmPassword.isBlank() -> "Please confirm your password"
                    !isConfirmPasswordValid -> "Passwords do not match"
                    else -> "Please check your input"
                }
            )
            return
        }
        
        viewModelScope.launch {
            _uiState.value = currentState.copy(isLoading = true, error = null)
            
            when (val result = registerUseCase(
                currentState.email, 
                currentState.password,
                currentState.confirmPassword
            )) {
                is Result.Success -> {
                    _uiState.value = RegisterUiState(
                        isSuccess = true,
                        createdUser = result.data
                    )
                }
                is Result.Error -> {
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
        _uiState.value = RegisterUiState()
    }
}
