package com.zama.safeops.frontend.presentation.screens.hazards

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.zama.safeops.frontend.domain.model.Hazard
import com.zama.safeops.frontend.domain.usecase.hazard.CreateHazardUseCase
import com.zama.safeops.frontend.domain.usecase.hazard.GetHazardsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HazardsViewModel(
    private val getHazardsUseCase: GetHazardsUseCase,
    private val createHazardUseCase: CreateHazardUseCase
) : ScreenModel {

    private val _hazards = MutableStateFlow<List<Hazard>>(emptyList())
    val hazards: StateFlow<List<Hazard>> = _hazards.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        loadHazards()
    }

    fun loadHazards(status: String? = null) {
        screenModelScope.launch {
            _isLoading.value = true
            _error.value = null

            getHazardsUseCase(status)
                .onSuccess { _hazards.value = it }
                .onFailure { _error.value = it.message }

            _isLoading.value = false
        }
    }

    fun createHazard(
        title: String,
        description: String?,
        category: String,
        severity: String,
        location: String?
    ) {
        screenModelScope.launch {
            _isLoading.value = true

            createHazardUseCase(title, description, category, severity, location)
                .onSuccess { loadHazards() }
                .onFailure { _error.value = it.message }

            _isLoading.value = false
        }
    }
}
