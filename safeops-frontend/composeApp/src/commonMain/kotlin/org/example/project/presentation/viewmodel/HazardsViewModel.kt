package org.example.project.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.example.project.domain.model.Hazard
import org.example.project.domain.model.HazardSeverity
import org.example.project.domain.model.HazardStatus
import org.example.project.domain.model.Result
import org.example.project.domain.usecase.GetHazardsUseCase

/**
 * Hazards ViewModel
 * Manages UI state for Hazards screen
 */
class HazardsViewModel(
    private val getHazardsUseCase: GetHazardsUseCase
) : ViewModel() {
    
    // UI State
    data class HazardsUiState(
        val isLoading: Boolean = true,
        val error: String? = null,
        val hazards: List<Hazard> = emptyList(),
        val selectedHazard: Hazard? = null,
        val statusFilter: HazardStatus? = null,
        val severityFilter: HazardSeverity? = null
    )
    
    private val _uiState = MutableStateFlow(HazardsUiState())
    val uiState: StateFlow<HazardsUiState> = _uiState.asStateFlow()
    
    init {
        loadHazards()
    }
    
    fun loadHazards() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            when (val result = getHazardsUseCase(
                status = _uiState.value.statusFilter,
                severity = _uiState.value.severityFilter
            )) {
                is Result.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        hazards = result.data,
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
    
    fun setStatusFilter(status: HazardStatus?) {
        _uiState.value = _uiState.value.copy(statusFilter = status)
        loadHazards()
    }
    
    fun setSeverityFilter(severity: HazardSeverity?) {
        _uiState.value = _uiState.value.copy(severityFilter = severity)
        loadHazards()
    }
    
    fun clearFilters() {
        _uiState.value = _uiState.value.copy(
            statusFilter = null,
            severityFilter = null
        )
        loadHazards()
    }
    
    fun refreshHazards() {
        loadHazards()
    }
    
    fun selectHazard(hazard: Hazard) {
        _uiState.value = _uiState.value.copy(selectedHazard = hazard)
    }
    
    fun clearSelection() {
        _uiState.value = _uiState.value.copy(selectedHazard = null)
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
