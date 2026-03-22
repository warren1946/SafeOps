package org.example.project.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.example.project.domain.model.Inspection
import org.example.project.domain.model.Result
import org.example.project.domain.usecase.GetInspectionsUseCase

/**
 * Inspections ViewModel
 * Manages UI state for Inspections screen
 */
class InspectionsViewModel(
    private val getInspectionsUseCase: GetInspectionsUseCase
) : ViewModel() {
    
    // UI State
    data class InspectionsUiState(
        val isLoading: Boolean = true,
        val error: String? = null,
        val inspections: List<Inspection> = emptyList(),
        val selectedInspection: Inspection? = null
    )
    
    private val _uiState = MutableStateFlow(InspectionsUiState())
    val uiState: StateFlow<InspectionsUiState> = _uiState.asStateFlow()
    
    init {
        loadInspections()
    }
    
    fun loadInspections() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            when (val result = getInspectionsUseCase()) {
                is Result.Success -> {
                    _uiState.value = InspectionsUiState(
                        isLoading = false,
                        inspections = result.data,
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
    
    fun refreshInspections() {
        loadInspections()
    }
    
    fun selectInspection(inspection: Inspection) {
        _uiState.value = _uiState.value.copy(selectedInspection = inspection)
    }
    
    fun clearSelection() {
        _uiState.value = _uiState.value.copy(selectedInspection = null)
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
