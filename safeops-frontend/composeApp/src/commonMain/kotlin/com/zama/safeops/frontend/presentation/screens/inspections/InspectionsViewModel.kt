package com.zama.safeops.frontend.presentation.screens.inspections

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.zama.safeops.frontend.domain.model.Inspection
import com.zama.safeops.frontend.domain.usecase.inspection.GetInspectionsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class InspectionsViewModel(
    private val getInspectionsUseCase: GetInspectionsUseCase
) : ScreenModel {

    private val _inspections = MutableStateFlow<List<Inspection>>(emptyList())
    val inspections: StateFlow<List<Inspection>> = _inspections.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        loadInspections()
    }

    fun loadInspections(status: String? = null) {
        screenModelScope.launch {
            _isLoading.value = true
            _error.value = null

            getInspectionsUseCase(status)
                .onSuccess { _inspections.value = it }
                .onFailure { _error.value = it.message }

            _isLoading.value = false
        }
    }
}
