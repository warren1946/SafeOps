/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 */

package com.zama.safeops.modules.engagement.application.services

import com.zama.safeops.modules.engagement.application.ports.GamificationPort
import com.zama.safeops.modules.engagement.domain.model.*
import com.zama.safeops.modules.tenant.domain.valueobjects.TenantId
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.time.Instant
import java.time.temporal.ChronoUnit

/**
 * Service for managing gamification, badges, and engagement.
 */
@Service
class GamificationService(
    private val gamificationPort: GamificationPort,
    private val notificationService: com.zama.safeops.modules.notification.application.services.NotificationService
) {

    /**
     * Get user's safety scorecard.
     */
    fun getScorecard(tenantId: TenantId, userId: Long): SafetyScorecard {
        return gamificationPort.getScorecard(tenantId.value, userId)
            ?: createNewScorecard(tenantId, userId)
    }

    /**
     * Get leaderboard.
     */
    fun getLeaderboard(
        tenantId: TenantId,
        type: com.zama.safeops.modules.engagement.domain.model.LeaderboardType = com.zama.safeops.modules.engagement.domain.model.LeaderboardType.OVERALL,
        timeFrame: TimeFrame = TimeFrame.MONTHLY,
        limit: Int = 10
    ): List<com.zama.safeops.modules.engagement.domain.model.LeaderboardEntry> {
        return gamificationPort.getLeaderboard(tenantId.value, type, timeFrame, limit)
    }

    /**
     * Process inspection completion for points.
     */
    fun processInspectionCompleted(
        tenantId: TenantId,
        userId: Long,
        inspectionId: Long,
        score: Int,
        isPerfect: Boolean
    ) {
        // Award points
        val points = when {
            isPerfect -> 100
            score >= 90 -> 75
            score >= 80 -> 50
            score >= 70 -> 25
            else -> 10
        }

        awardPoints(
            tenantId, userId, points, TransactionType.INSPECTION_COMPLETED,
            "Inspection #$inspectionId completed with score $score%", inspectionId.toString()
        )

        if (isPerfect) {
            awardPoints(
                tenantId, userId, 50, TransactionType.INSPECTION_PERFECT_SCORE,
                "Perfect score bonus!", inspectionId.toString()
            )
        }

        // Update streak
        updateInspectionStreak(tenantId, userId)

        // Check for badges
        checkAndAwardBadges(tenantId, userId)
    }

    /**
     * Process hazard report for points.
     */
    fun processHazardReported(
        tenantId: TenantId,
        userId: Long,
        hazardId: Long,
        severity: com.zama.safeops.modules.hazards.domain.model.HazardSeverity
    ) {
        val points = when (severity) {
            com.zama.safeops.modules.hazards.domain.model.HazardSeverity.CRITICAL -> 100
            com.zama.safeops.modules.hazards.domain.model.HazardSeverity.HIGH -> 50
            com.zama.safeops.modules.hazards.domain.model.HazardSeverity.MEDIUM -> 25
            com.zama.safeops.modules.hazards.domain.model.HazardSeverity.LOW -> 10
        }

        awardPoints(
            tenantId, userId, points, TransactionType.HAZARD_REPORTED,
            "Hazard #$hazardId reported", hazardId.toString()
        )

        checkAndAwardBadges(tenantId, userId)
    }

    /**
     * Process hazard resolution.
     */
    fun processHazardResolved(
        tenantId: TenantId,
        userId: Long,
        hazardId: Long
    ) {
        awardPoints(
            tenantId, userId, 25, TransactionType.HAZARD_RESOLVED,
            "Hazard #$hazardId resolved", hazardId.toString()
        )
    }

    /**
     * Create a new challenge.
     */
    fun createChallenge(
        tenantId: TenantId,
        name: String,
        description: String,
        type: ChallengeType,
        criteria: ChallengeCriteria,
        durationDays: Int,
        rewards: ChallengeRewards
    ): SafetyChallenge {
        val challenge = SafetyChallenge(
            id = java.util.UUID.randomUUID().toString(),
            tenantId = tenantId.value,
            name = name,
            description = description,
            type = type,
            criteria = criteria,
            startDate = Instant.now(),
            endDate = Instant.now().plus(durationDays.toLong(), ChronoUnit.DAYS),
            participants = emptyList(),
            rewards = rewards,
            status = ChallengeStatus.ACTIVE
        )

        return gamificationPort.saveChallenge(challenge)
    }

    /**
     * Join a challenge.
     */
    fun joinChallenge(
        tenantId: TenantId,
        challengeId: String,
        userId: Long
    ): SafetyChallenge {
        return gamificationPort.addParticipant(challengeId, userId)
    }

    /**
     * Get active challenges.
     */
    fun getActiveChallenges(tenantId: TenantId): List<SafetyChallenge> {
        return gamificationPort.getActiveChallenges(tenantId.value)
    }

    /**
     * Get user's badges.
     */
    fun getUserBadges(tenantId: TenantId, userId: Long): List<Badge> {
        return gamificationPort.getUserBadges(tenantId.value, userId)
    }

    /**
     * Get badge progress.
     */
    fun getBadgeProgress(tenantId: TenantId, userId: Long): List<BadgeProgressView> {
        val scorecard = getScorecard(tenantId, userId)
        val earnedBadgeIds = scorecard.badges.map { it.id }.toSet()

        return BadgeCatalog.ALL_BADGES.map { badgeDef ->
            val isEarned = earnedBadgeIds.contains(badgeDef.id)
            val progress = if (isEarned) {
                BadgeProgress(badgeDef.criteria.targetValue, badgeDef.criteria.targetValue, 100.0)
            } else {
                calculateProgress(tenantId, userId, badgeDef.criteria)
            }

            BadgeProgressView(
                badge = badgeDef,
                progress = progress,
                isEarned = isEarned
            )
        }
    }

    /**
     * Daily streak check.
     */
    @Scheduled(cron = "0 0 6 * * ?") // Every day at 6 AM
    fun checkDailyStreaks() {
        // Reset streaks for users who missed a day
        // Award streak bonuses
    }

    /**
     * Weekly challenge evaluation.
     */
    @Scheduled(cron = "0 0 9 * * MON") // Every Monday at 9 AM
    fun evaluateWeeklyChallenges() {
        val completedChallenges = gamificationPort.getCompletedChallenges()

        completedChallenges.forEach { challenge ->
            // Determine winners
            val winners = challenge.participants
                .sortedByDescending { it.currentScore }
                .take(3)

            // Award prizes
            winners.forEach { participant ->
                participant.userId?.let { userId ->
                    awardPoints(
                        TenantId(challenge.tenantId),
                        userId,
                        challenge.rewards.points,
                        TransactionType.CHALLENGE_WON,
                        "Won challenge: ${challenge.name}",
                        challenge.id
                    )

                    // Notify winner
                    notificationService.sendTemplatedNotification(
                        tenantId = TenantId(challenge.tenantId),
                        recipient = com.zama.safeops.modules.notification.domain.model.Recipient(userId = userId),
                        channel = com.zama.safeops.modules.notification.domain.model.NotificationChannel.PUSH,
                        templateId = "challenge_won",
                        templateData = mapOf(
                            "challengeName" to challenge.name,
                            "rank" to participant.rank.toString(),
                            "points" to challenge.rewards.points.toString()
                        )
                    )
                }
            }
        }
    }

    private fun createNewScorecard(tenantId: TenantId, userId: Long): SafetyScorecard {
        return SafetyScorecard(
            userId = userId,
            tenantId = tenantId.value,
            totalScore = 0,
            level = 1,
            rank = 0,
            badges = emptyList(),
            achievements = emptyList(),
            currentStreak = StreakInfo(StreakType.DAILY_INSPECTION, 0, 0, Instant.now(), Instant.now()),
            longestStreak = StreakInfo(StreakType.DAILY_INSPECTION, 0, 0, Instant.now(), Instant.now()),
            teamRank = null,
            shiftRank = null
        )
    }

    private fun awardPoints(
        tenantId: TenantId,
        userId: Long,
        points: Int,
        type: TransactionType,
        description: String,
        referenceId: String?
    ) {
        val transaction = PointsTransaction(
            id = java.util.UUID.randomUUID().toString(),
            userId = userId,
            tenantId = tenantId.value,
            points = points,
            type = type,
            description = description,
            referenceId = referenceId
        )

        gamificationPort.recordTransaction(transaction)
        gamificationPort.addPoints(tenantId.value, userId, points)
    }

    private fun updateInspectionStreak(tenantId: TenantId, userId: Long) {
        val scorecard = getScorecard(tenantId, userId)
        val streak = scorecard.currentStreak

        val lastActivity = streak.lastActivityAt
        val yesterday = Instant.now().minus(1, ChronoUnit.DAYS)

        val newStreak = if (lastActivity.isAfter(yesterday)) {
            // Continue streak
            streak.copy(
                currentCount = streak.currentCount + 1,
                longestCount = maxOf(streak.longestCount, streak.currentCount + 1),
                lastActivityAt = Instant.now()
            )
        } else {
            // Reset streak
            streak.copy(
                currentCount = 1,
                lastActivityAt = Instant.now()
            )
        }

        gamificationPort.updateStreak(tenantId.value, userId, newStreak)

        // Award streak bonus
        when (newStreak.currentCount) {
            7 -> awardPoints(tenantId, userId, 100, TransactionType.STREAK_BONUS, "7-day streak bonus!", null)
            30 -> awardPoints(tenantId, userId, 500, TransactionType.STREAK_BONUS, "30-day streak bonus!", null)
            100 -> awardPoints(tenantId, userId, 2000, TransactionType.STREAK_BONUS, "100-day streak bonus!", null)
        }
    }

    private fun checkAndAwardBadges(tenantId: TenantId, userId: Long) {
        val scorecard = getScorecard(tenantId, userId)
        val earnedBadgeIds = scorecard.badges.map { it.id }.toSet()

        BadgeCatalog.ALL_BADGES.forEach { badgeDef ->
            if (!earnedBadgeIds.contains(badgeDef.id)) {
                if (hasMetCriteria(tenantId, userId, badgeDef.criteria)) {
                    awardBadge(tenantId, userId, badgeDef)
                }
            }
        }
    }

    private fun hasMetCriteria(tenantId: TenantId, userId: Long, criteria: AchievementCriteria): Boolean {
        val progress = calculateProgress(tenantId, userId, criteria)
        return progress.isComplete()
    }

    private fun calculateProgress(tenantId: TenantId, userId: Long, criteria: AchievementCriteria): BadgeProgress {
        val currentValue = when (criteria.type) {
            CriteriaType.INSPECTIONS_COMPLETED ->
                gamificationPort.getCompletedInspectionsCount(tenantId.value, userId, criteria.timeFrame)

            CriteriaType.INSPECTIONS_PERFECT_SCORE ->
                gamificationPort.getPerfectInspectionsCount(tenantId.value, userId, criteria.timeFrame)

            CriteriaType.HAZARDS_REPORTED ->
                gamificationPort.getReportedHazardsCount(tenantId.value, userId, criteria.timeFrame)

            CriteriaType.HAZARDS_CRITICAL_FOUND ->
                gamificationPort.getCriticalHazardsCount(tenantId.value, userId, criteria.timeFrame)

            CriteriaType.CONSECUTIVE_DAYS_ACTIVE ->
                getScorecard(tenantId, userId).currentStreak.currentCount

            else -> 0
        }

        val percentage = (currentValue.toDouble() / criteria.targetValue * 100).coerceAtMost(100.0)

        return BadgeProgress(currentValue, criteria.targetValue, percentage)
    }

    private fun awardBadge(tenantId: TenantId, userId: Long, badgeDef: BadgeDefinition) {
        val badge = Badge(
            id = badgeDef.id,
            name = badgeDef.name,
            description = badgeDef.description,
            iconUrl = badgeDef.iconUrl ?: "/badges/${badgeDef.id}.png",
            rarity = badgeDef.rarity,
            category = badgeDef.category,
            earnedAt = Instant.now()
        )

        gamificationPort.addBadge(tenantId.value, userId, badge)

        // Award badge bonus points
        val points = when (badgeDef.rarity) {
            BadgeRarity.COMMON -> 50
            BadgeRarity.UNCOMMON -> 100
            BadgeRarity.RARE -> 250
            BadgeRarity.EPIC -> 500
            BadgeRarity.LEGENDARY -> 1000
        }

        awardPoints(
            tenantId, userId, points, TransactionType.BADGE_EARNED,
            "Earned badge: ${badgeDef.name}", badgeDef.id
        )

        // Notify user
        notificationService.sendTemplatedNotification(
            tenantId = tenantId,
            recipient = com.zama.safeops.modules.notification.domain.model.Recipient(userId = userId),
            channel = com.zama.safeops.modules.notification.domain.model.NotificationChannel.PUSH,
            templateId = "badge_earned",
            templateData = mapOf(
                "badgeName" to badgeDef.name,
                "rarity" to badgeDef.rarity.name,
                "points" to points.toString()
            )
        )
    }
}

// DTOs
data class BadgeProgressView(
    val badge: BadgeDefinition,
    val progress: BadgeProgress,
    val isEarned: Boolean
)
