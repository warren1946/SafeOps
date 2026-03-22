package org.example.project.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.example.project.domain.model.DashboardStatistics
import org.example.project.domain.model.Result
import org.example.project.domain.model.User
import org.example.project.domain.usecase.GetCurrentUserUseCase
import org.example.project.domain.usecase.GetDashboardStatisticsUseCase
import org.example.project.domain.usecase.LogoutUseCase

/**
 * Dashboard ViewModel
 * Manages UI state and business logic for Dashboard screen
 */
class DashboardViewModel(
    private val getDashboardStatisticsUseCase: GetDashboardStatisticsUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val logoutUseCase: LogoutUseCase
) : ViewModel() {
    
    // UI State
    data class DashboardUiState(
        val isLoading: Boolean = true,
        val error: String? = null,
        val statistics: DashboardStatistics? = null,
        val currentUser: User? = null,
        val isLoggedOut: Boolean = false
    )
    
    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()
    
    init {
        loadDashboardData()
        observeCurrentUser()
    }
    
    private fun observeCurrentUser() {
        getCurrentUserUseCase()
            .onEach { result ->
                when (result) {
                    is Result.Success -> {
                        _uiState.value = _uiState.value.copy(currentUser = result.data)
                    }
                    else -> { /* Handle error if needed */ }
                }
            }
            .launchIn(viewModelScope)
    }
    
    fun loadDashboardData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            when (val result = getDashboardStatisticsUseCase()) {
                is Result.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        statistics = result.data,
                        error = null
                    )
                }
                is Result.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = result.message
                    )
                }
                is Result.Loading -> {
                    _uiState.value = _uiState.value.copy(isLoading = true)
                }
            }
        }
    }
    
    fun refreshData() {
        loadDashboardData()
    }
    
    fun logout() {
        viewModelScope.launch {
            when (val result = logoutUseCase()) {
                is Result.Success -> {
                    _uiState.value = DashboardUiState(isLoggedOut = true)
                }
                is Result.Error -> {
                    _uiState.value = _uiState.value.copy(error = result.message)
                }
                else -> { /* Handle loading if needed */ }
            }
        }
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
    
    fun resetState() {
        _uiState.value = DashboardUiState()
    }
}
