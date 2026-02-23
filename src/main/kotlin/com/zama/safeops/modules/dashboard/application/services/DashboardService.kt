/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 * Permission granted for use, modification, and distribution with attribution.
 * No warranty provided.
 */

package com.zama.safeops.modules.dashboard.application.services

import com.zama.safeops.modules.auth.application.ports.UserPort
import com.zama.safeops.modules.dashboard.api.dto.DashboardFilterRequest
import com.zama.safeops.modules.dashboard.api.dto.FilterType
import com.zama.safeops.modules.dashboard.application.extensions.toSafetyLocationType
import com.zama.safeops.modules.dashboard.domain.model.*
import com.zama.safeops.modules.hazards.application.ports.HazardPort
import com.zama.safeops.modules.hazards.domain.model.HazardStatus
import com.zama.safeops.modules.inspections.api.mappers.calculateScore
import com.zama.safeops.modules.inspections.api.mappers.toSummaryResponse
import com.zama.safeops.modules.inspections.application.ports.InspectionPort
import com.zama.safeops.modules.inspections.application.services.InspectionQueryService
import com.zama.safeops.modules.inspections.domain.model.InspectionStatus
import com.zama.safeops.modules.inspections.domain.model.InspectionSummaryResponse
import com.zama.safeops.modules.safety.application.ports.SafetyAlertPort
import com.zama.safeops.modules.safety.application.ports.SafetyEventPort
import com.zama.safeops.modules.safety.domain.model.SafetyEventType
import com.zama.safeops.modules.safety.domain.model.SafetyLocationType
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.time.ZoneOffset
import java.time.temporal.ChronoUnit

@Service
class DashboardService(
    private val hazardPort: HazardPort,
    private val eventPort: SafetyEventPort,
    private val inspectionPort: InspectionPort,
    private val alertPort: SafetyAlertPort,
    private val userPort: UserPort,
    private val inspectionQueryService: InspectionQueryService
) {

    @Transactional(readOnly = true)
    fun getActiveHazards(limit: Int): List<DashboardHazardSummary> =
        hazardPort.findActive(limit).map {
            DashboardHazardSummary(
                id = it.id?.value ?: error("Hazard ID must not be null"),
                title = it.title.value,
                status = it.status.name,
                assignedTo = it.assignedTo,
                createdAt = it.createdAt
            )
        }

    @Transactional(readOnly = true)
    fun getSummary(): DashboardSummary {
        val activeHazards = hazardPort.findActive(limit = 1000) // cheap-ish count
        val inspections = inspectionPort.findAll()
        val alerts = alertPort.findUnacknowledged()
        val recentEvents = eventPort.findRecent(limit = 50)
        val activeOfficers = userPort.countActiveOfficers()

        return DashboardSummary(
            activeHazardCount = activeHazards.size,
            submittedInspectionCount = inspections.count { it.status == InspectionStatus.SUBMITTED },
            unacknowledgedAlertCount = alerts.size,
            recentEventCount = recentEvents.size,
            activeOfficerCount = activeOfficers
        )
    }

    @Transactional(readOnly = true)
    fun getEventTrends(): List<DashboardEventTrendPoint> {
        val now = Instant.now()
        val start = now.minusSeconds(30L * 24 * 60 * 60) // last 30 days
        val events = eventPort.findByPeriod(start, now)

        return events
            .groupBy { it.createdAt.atZone(ZoneOffset.UTC).toLocalDate() }
            .toSortedMap()
            .map { (date, dayEvents) ->
                DashboardEventTrendPoint(
                    date = date,
                    total = dayEvents.size,
                    incidents = dayEvents.count { it.type == SafetyEventType.INCIDENT },
                    nearMisses = dayEvents.count { it.type == SafetyEventType.NEAR_MISS },
                    unsafeConditions = dayEvents.count { it.type == SafetyEventType.UNSAFE_CONDITION },
                    unsafeActs = dayEvents.count { it.type == SafetyEventType.UNSAFE_ACT },
                    observations = dayEvents.count { it.type == SafetyEventType.OBSERVATION }
                )
            }
    }

    @Transactional(readOnly = true)
    fun getSummaryFiltered(filter: DashboardFilterRequest): DashboardSummary {
        val events = eventPort.findAll()
            .filter { matchesFilter(filter, it.locationType, it.locationId) }

        val inspections = inspectionPort.findAll()
            .filter { matchesFilter(filter, it.targetType.toSafetyLocationType(), it.targetId) }

        val alerts = alertPort.findUnacknowledged()
            .filter { alert ->
                val event = eventPort.findById(alert.eventId) ?: return@filter false
                matchesFilter(filter, event.locationType, event.locationId)
            }

        return DashboardSummary(
            activeHazardCount = 0,
            submittedInspectionCount = inspections.count { it.status == InspectionStatus.SUBMITTED },
            unacknowledgedAlertCount = alerts.size,
            recentEventCount = events.size,
            activeOfficerCount = userPort.countActiveOfficers()
        )
    }

    @Transactional(readOnly = true)
    fun getEventTrendsFiltered(filter: DashboardFilterRequest): List<DashboardEventTrendPoint> {
        val now = Instant.now()
        val start = now.minusSeconds(30L * 24 * 60 * 60)

        val events = eventPort.findByPeriod(start, now)
            .filter { matchesFilter(filter, it.locationType, it.locationId) }

        return events
            .groupBy { it.createdAt.atZone(ZoneOffset.UTC).toLocalDate() }
            .toSortedMap()
            .map { (date, dayEvents) ->
                DashboardEventTrendPoint(
                    date = date,
                    total = dayEvents.size,
                    incidents = dayEvents.count { it.type == SafetyEventType.INCIDENT },
                    nearMisses = dayEvents.count { it.type == SafetyEventType.NEAR_MISS },
                    unsafeConditions = dayEvents.count { it.type == SafetyEventType.UNSAFE_CONDITION },
                    unsafeActs = dayEvents.count { it.type == SafetyEventType.UNSAFE_ACT },
                    observations = dayEvents.count { it.type == SafetyEventType.OBSERVATION }
                )
            }
    }

    private fun DashboardFilterRequest.toLocationType(): SafetyLocationType =
        when (type) {
            FilterType.AREA -> SafetyLocationType.AREA
            FilterType.SHAFT -> SafetyLocationType.SHAFT
            FilterType.SITE -> SafetyLocationType.SITE
        }

    fun getActiveHazardsFiltered(filter: DashboardFilterRequest): List<DashboardHazardSummary> = hazardPort
        .findByLocation(filter.toLocationType(), filter.id)
        .filter { it.status != HazardStatus.RESOLVED }
        .sortedByDescending { it.createdAt }
        .take(5)
        .map {
            DashboardHazardSummary(
                id = it.id!!.value,
                title = it.title.value,
                status = it.status.name,
                assignedTo = it.assignedTo,
                createdAt = it.createdAt
            )
        }

    private fun matchesFilter(
        filter: DashboardFilterRequest,
        locationType: SafetyLocationType,
        locationId: Long
    ): Boolean {
        return when (filter.type) {
            FilterType.AREA -> locationType == SafetyLocationType.AREA && locationId == filter.id
            FilterType.SHAFT -> locationType == SafetyLocationType.SHAFT && locationId == filter.id
            FilterType.SITE -> locationType == SafetyLocationType.SITE && locationId == filter.id
        }
    }

    fun getTopFailingInspections(limit: Int = 5): List<InspectionSummaryResponse> {
        val recent = inspectionPort.findRecent(50)
        val withItems = inspectionQueryService.attachItems(recent)

        return withItems
            .map { it to it.calculateScore() }
            .sortedBy { it.second.percentage }
            .take(limit)
            .map { it.first.toSummaryResponse() }
    }

    fun getInspectionScoreTrend(days: Long = 30): List<ScoreTrendPoint> {
        val since = Instant.now().minus(days, ChronoUnit.DAYS)
        val recent = inspectionPort.findSince(since, PageRequest.of(0, 200, Sort.by("createdAt").descending()))
        val withItems = inspectionQueryService.attachItems(recent)

        return withItems
            .groupBy { it.createdAt.atZone(ZoneOffset.UTC).toLocalDate() }
            .map { (date, inspections) ->
                val scores = inspections.map { it.calculateScore().percentage }
                ScoreTrendPoint(
                    date = date,
                    averageScore = if (scores.isEmpty()) 0.0 else scores.average()
                )
            }
            .sortedBy { it.date }
    }

    fun getReviewerCommentsSummary(limit: Int = 10): List<ReviewerCommentSummary> {
        val recent = inspectionPort.findRecent(100)
        return recent
            .filter { !it.reviewerComments.isNullOrBlank() }
            .sortedByDescending { it.updatedAt }
            .take(limit)
            .map {
                ReviewerCommentSummary(
                    inspectionId = it.id!!.value,
                    title = it.title,
                    reviewerId = it.assignedReviewerId,
                    comment = it.reviewerComments!!,
                    updatedAt = it.updatedAt
                )
            }
    }
}