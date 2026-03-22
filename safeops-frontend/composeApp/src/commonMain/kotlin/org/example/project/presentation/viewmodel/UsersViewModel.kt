package org.example.project.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.example.project.domain.model.Result
import org.example.project.domain.model.User
import org.example.project.domain.model.UserRole
import org.example.project.domain.usecase.GetUsersUseCase

/**
 * Users ViewModel
 * Manages UI state for Users screen
 */
class UsersViewModel(
    private val getUsersUseCase: GetUsersUseCase
) : ViewModel() {
    
    // UI State
    data class UsersUiState(
        val isLoading: Boolean = true,
        val error: String? = null,
        val users: List<User> = emptyList(),
        val selectedUser: User? = null,
        val roleFilter: UserRole? = null,
        val searchQuery: String = ""
    )
    
    private val _uiState = MutableStateFlow(UsersUiState())
    val uiState: StateFlow<UsersUiState> = _uiState.asStateFlow()
    
    // Filtered users based on search and role filter
    val filteredUsers: List<User>
        get() {
            var result = _uiState.value.users
            
            // Apply role filter
            _uiState.value.roleFilter?.let { role ->
                result = result.filter { it.roles.contains(role) }
            }
            
            // Apply search filter
            if (_uiState.value.searchQuery.isNotBlank()) {
                val query = _uiState.value.searchQuery.lowercase()
                result = result.filter { 
                    it.email.lowercase().contains(query)
                }
            }
            
            return result
        }
    
    init {
        loadUsers()
    }
    
    fun loadUsers() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            when (val result = getUsersUseCase()) {
                is Result.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        users = result.data,
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
    
    fun setRoleFilter(role: UserRole?) {
        _uiState.value = _uiState.value.copy(roleFilter = role)
    }
    
    fun setSearchQuery(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
    }
    
    fun clearFilters() {
        _uiState.value = _uiState.value.copy(
            roleFilter = null,
            searchQuery = ""
        )
    }
    
    fun refreshUsers() {
        loadUsers()
    }
    
    fun selectUser(user: User) {
        _uiState.value = _uiState.value.copy(selectedUser = user)
    }
    
    fun clearSelection() {
        _uiState.value = _uiState.value.copy(selectedUser = null)
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
