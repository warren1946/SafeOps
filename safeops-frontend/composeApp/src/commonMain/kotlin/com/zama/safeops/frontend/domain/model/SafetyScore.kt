package com.zama.safeops.frontend.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class SafetyScore(
    val overallScore: Int,
    val previousScore: Int,
    val trend: TrendDirection,
    val inspectionsCompleted: Int,
    val inspectionsScheduled: Int,
    val openHazards: Int,
    val resolvedHazardsThisMonth: Int,
    val incidentCount: Int,
    val daysSinceLastIncident: Int,
    val departmentScores: List<DepartmentScore> = emptyList()
)

@Serializable
data class DepartmentScore(
    val departmentName: String,
    val score: Int,
    val trend: TrendDirection
)

@Serializable
enum class TrendDirection {
    UP,
    DOWN,
    STABLE
}
