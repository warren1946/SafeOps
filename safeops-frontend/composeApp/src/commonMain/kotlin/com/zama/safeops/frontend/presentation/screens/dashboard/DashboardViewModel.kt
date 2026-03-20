package com.zama.safeops.frontend.presentation.screens.dashboard

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.zama.safeops.frontend.domain.model.Hazard
import com.zama.safeops.frontend.domain.model.Inspection
import com.zama.safeops.frontend.domain.model.SafetyScore
import com.zama.safeops.frontend.domain.model.TrendDirection
import com.zama.safeops.frontend.domain.usecase.hazard.GetHazardsUseCase
import com.zama.safeops.frontend.domain.usecase.inspection.GetInspectionsUseCase
import com.zama.safeops.frontend.domain.usecase.safety.GetSafetyScoreUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DashboardViewModel(
    private val getSafetyScoreUseCase: GetSafetyScoreUseCase,
    private val getInspectionsUseCase: GetInspectionsUseCase,
    private val getHazardsUseCase: GetHazardsUseCase
) : ScreenModel {

    private val _safetyScore = MutableStateFlow<SafetyScore?>(null)
    val safetyScore: StateFlow<SafetyScore?> = _safetyScore.asStateFlow()

    private val _recentInspections = MutableStateFlow<List<Inspection>>(emptyList())
    val recentInspections: StateFlow<List<Inspection>> = _recentInspections.asStateFlow()

    private val _openHazards = MutableStateFlow<List<Hazard>>(emptyList())
    val openHazards: StateFlow<List<Hazard>> = _openHazards.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        loadDashboardData()
    }

    fun loadDashboardData() {
        screenModelScope.launch {
            _isLoading.value = true
            _error.value = null

            // Load safety score
            getSafetyScoreUseCase()
                .onSuccess { _safetyScore.value = it }
                .onFailure { _error.value = it.message }

            // Load recent inspections
            getInspectionsUseCase()
                .onSuccess { _recentInspections.value = it.take(5) }

            // Load open hazards
            getHazardsUseCase("OPEN")
                .onSuccess { _openHazards.value = it.take(5) }

            _isLoading.value = false
        }
    }

    fun getTrendIcon(trend: TrendDirection): String = when (trend) {
        TrendDirection.UP -> "↑"
        TrendDirection.DOWN -> "↓"
        TrendDirection.STABLE -> "→"
    }

    fun getTrendColor(trend: TrendDirection): String = when (trend) {
        TrendDirection.UP -> "green"
        TrendDirection.DOWN -> "red"
        TrendDirection.STABLE -> "gray"
    }
}
