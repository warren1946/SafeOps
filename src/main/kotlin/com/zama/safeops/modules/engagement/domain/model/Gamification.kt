/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 */

package com.zama.safeops.modules.engagement.domain.model

import java.time.Instant

/**
 * User's safety score and ranking.
 */
data class SafetyScorecard(
    val userId: Long,
    val tenantId: Long,
    val totalScore: Int,
    val level: Int,
    val rank: Int,
    val badges: List<Badge>,
    val achievements: List<Achievement>,
    val currentStreak: StreakInfo,
    val longestStreak: StreakInfo,
    val teamRank: Int?,
    val shiftRank: Int?,
    val lastUpdated: Instant = Instant.now()
) {
    fun calculateLevel(): Int {
        // Level up every 1000 points
        return (totalScore / 1000) + 1
    }

    fun getTitle(): String = when (level) {
        1 -> "Safety Novice"
        2 -> "Safety Aware"
        3 -> "Safety Guardian"
        4 -> "Safety Champion"
        5 -> "Safety Master"
        else -> "Safety Legend"
    }
}

/**
 * Badge earned by user.
 */
data class Badge(
    val id: String,
    val name: String,
    val description: String,
    val iconUrl: String,
    val rarity: BadgeRarity,
    val category: BadgeCategory,
    val earnedAt: Instant,
    val progress: BadgeProgress? = null
)

enum class BadgeRarity {
    COMMON,      // White
    UNCOMMON,    // Green
    RARE,        // Blue
    EPIC,        // Purple
    LEGENDARY    // Gold
}

enum class BadgeCategory {
    INSPECTION,   // Inspection-related
    HAZARD,       // Hazard reporting
    CONSISTENCY,  // Streaks, regular activity
    TEAMWORK,     // Team challenges
    SPECIAL       // Limited time, special events
}

data class BadgeProgress(
    val currentValue: Int,
    val targetValue: Int,
    val percentage: Double
) {
    fun isComplete(): Boolean = currentValue >= targetValue
}

/**
 * Achievement criteria and rewards.
 */
data class Achievement(
    val id: String,
    val name: String,
    val description: String,
    val criteria: AchievementCriteria,
    val pointsReward: Int,
    val badgeReward: String? = null,
    val unlockedAt: Instant? = null
)

data class AchievementCriteria(
    val type: CriteriaType,
    val targetValue: Int,
    val timeFrame: TimeFrame? = null
)

enum class CriteriaType {
    INSPECTIONS_COMPLETED,
    INSPECTIONS_PERFECT_SCORE,
    HAZARDS_REPORTED,
    HAZARDS_CRITICAL_FOUND,
    DAYS_WITHOUT_INCIDENT,
    CONSECUTIVE_DAYS_ACTIVE,
    PHOTOS_UPLOADED,
    WHATSAPP_INSPECTIONS,
    TEAM_CHALLENGE_WINS,
    EARLY_BIRD
}

enum class TimeFrame {
    DAILY,
    WEEKLY,
    MONTHLY,
    QUARTERLY,
    YEARLY,
    ALL_TIME
}

// DTOs for leaderboard
enum class LeaderboardType {
    OVERALL,
    INSPECTIONS,
    HAZARDS,
    STREAK
}

data class LeaderboardEntry(
    val rank: Int,
    val userId: Long,
    val userName: String,
    val score: Int,
    val level: Int,
    val badges: Int,
    val trend: Trend
)

enum class Trend {
    UP, DOWN, STABLE
}

/**
 * Streak information.
 */
data class StreakInfo(
    val type: StreakType,
    val currentCount: Int,
    val longestCount: Int,
    val startedAt: Instant,
    val lastActivityAt: Instant
)

enum class StreakType {
    DAILY_INSPECTION,     // Inspection every day
    DAYS_SAFE,            // Days without incidents
    HAZARD_REPORTING,     // Consecutive days reporting hazards
    EARLY_BIRD            // Early morning inspections
}

/**
 * Team or shift challenge.
 */
data class SafetyChallenge(
    val id: String,
    val tenantId: Long,
    val name: String,
    val description: String,
    val type: ChallengeType,
    val criteria: ChallengeCriteria,
    val startDate: Instant,
    val endDate: Instant,
    val participants: List<ChallengeParticipant>,
    val rewards: ChallengeRewards,
    val status: ChallengeStatus
)

enum class ChallengeType {
    INDIVIDUAL,   // Single officer
    TEAM,         // Shift vs shift
    LOCATION      // Site vs site
}

data class ChallengeCriteria(
    val metric: ChallengeMetric,
    val targetValue: Int
)

enum class ChallengeMetric {
    TOTAL_INSPECTIONS,
    AVERAGE_SCORE,
    HAZARDS_FOUND,
    PERFECT_SCORES,
    RESPONSE_TIME
}

data class ChallengeParticipant(
    val userId: Long?,
    val teamId: String?,
    val currentScore: Int,
    val rank: Int
)

data class ChallengeRewards(
    val points: Int,
    val badge: String?,
    val title: String?
)

enum class ChallengeStatus {
    UPCOMING,
    ACTIVE,
    COMPLETED,
    CANCELLED
}

/**
 * Points transaction history.
 */
data class PointsTransaction(
    val id: String,
    val userId: Long,
    val tenantId: Long,
    val points: Int,  // Can be negative for deductions
    val type: TransactionType,
    val description: String,
    val referenceId: String?,  // Related entity (inspection, hazard, etc.)
    val createdAt: Instant = Instant.now()
)

enum class TransactionType {
    INSPECTION_COMPLETED,
    INSPECTION_PERFECT_SCORE,
    HAZARD_REPORTED,
    HAZARD_RESOLVED,
    STREAK_BONUS,
    CHALLENGE_WON,
    BADGE_EARNED,
    REFERRAL,
    DEDUCTION  // For corrections
}

/**
 * Predefined badges.
 */
object BadgeCatalog {
    val FIRST_INSPECTION = BadgeDefinition(
        id = "first_inspection",
        name = "First Steps",
        description = "Complete your first inspection",
        rarity = BadgeRarity.COMMON,
        category = BadgeCategory.INSPECTION,
        criteria = AchievementCriteria(CriteriaType.INSPECTIONS_COMPLETED, 1)
    )

    val INSPECTOR_50 = BadgeDefinition(
        id = "inspector_50",
        name = "Dedicated Inspector",
        description = "Complete 50 inspections",
        rarity = BadgeRarity.UNCOMMON,
        category = BadgeCategory.INSPECTION,
        criteria = AchievementCriteria(CriteriaType.INSPECTIONS_COMPLETED, 50)
    )

    val INSPECTOR_100 = BadgeDefinition(
        id = "inspector_100",
        name = "Century Inspector",
        description = "Complete 100 inspections",
        rarity = BadgeRarity.RARE,
        category = BadgeCategory.INSPECTION,
        criteria = AchievementCriteria(CriteriaType.INSPECTIONS_COMPLETED, 100)
    )

    val PERFECT_SCORE_10 = BadgeDefinition(
        id = "perfect_10",
        name = "Perfectionist",
        description = "Achieve 10 perfect inspection scores (100%)",
        rarity = BadgeRarity.RARE,
        category = BadgeCategory.INSPECTION,
        criteria = AchievementCriteria(CriteriaType.INSPECTIONS_PERFECT_SCORE, 10)
    )

    val HAZARD_HUNTER = BadgeDefinition(
        id = "hazard_hunter",
        name = "Hazard Hunter",
        description = "Report 25 safety hazards",
        rarity = BadgeRarity.UNCOMMON,
        category = BadgeCategory.HAZARD,
        criteria = AchievementCriteria(CriteriaType.HAZARDS_REPORTED, 25)
    )

    val CRITICAL_EYE = BadgeDefinition(
        id = "critical_eye",
        name = "Critical Eye",
        description = "Report 5 critical hazards that prevented incidents",
        rarity = BadgeRarity.EPIC,
        category = BadgeCategory.HAZARD,
        criteria = AchievementCriteria(CriteriaType.HAZARDS_CRITICAL_FOUND, 5)
    )

    val WEEK_STREAK = BadgeDefinition(
        id = "week_streak",
        name = "Week Warrior",
        description = "7 consecutive days of inspections",
        rarity = BadgeRarity.UNCOMMON,
        category = BadgeCategory.CONSISTENCY,
        criteria = AchievementCriteria(CriteriaType.CONSECUTIVE_DAYS_ACTIVE, 7)
    )

    val MONTH_STREAK = BadgeDefinition(
        id = "month_streak",
        name = "Monthly Master",
        description = "30 consecutive days of inspections",
        rarity = BadgeRarity.RARE,
        category = BadgeCategory.CONSISTENCY,
        criteria = AchievementCriteria(CriteriaType.CONSECUTIVE_DAYS_ACTIVE, 30)
    )

    val EARLY_BIRD = BadgeDefinition(
        id = "early_bird",
        name = "Early Bird",
        description = "Complete 20 inspections before 7 AM",
        rarity = BadgeRarity.UNCOMMON,
        category = BadgeCategory.SPECIAL,
        criteria = AchievementCriteria(CriteriaType.EARLY_BIRD, 20)
    )

    val TEAM_PLAYER = BadgeDefinition(
        id = "team_player",
        name = "Team Player",
        description = "Win 3 team challenges",
        rarity = BadgeRarity.RARE,
        category = BadgeCategory.TEAMWORK,
        criteria = AchievementCriteria(CriteriaType.TEAM_CHALLENGE_WINS, 3)
    )

    val LEGENDARY_SAFE = BadgeDefinition(
        id = "legendary_safe",
        name = "Safety Legend",
        description = "365 days without any incidents in your area",
        rarity = BadgeRarity.LEGENDARY,
        category = BadgeCategory.CONSISTENCY,
        criteria = AchievementCriteria(CriteriaType.DAYS_WITHOUT_INCIDENT, 365)
    )

    val ALL_BADGES = listOf(
        FIRST_INSPECTION, INSPECTOR_50, INSPECTOR_100,
        PERFECT_SCORE_10, HAZARD_HUNTER, CRITICAL_EYE,
        WEEK_STREAK, MONTH_STREAK, EARLY_BIRD,
        TEAM_PLAYER, LEGENDARY_SAFE
    )
}

data class BadgeDefinition(
    val id: String,
    val name: String,
    val description: String,
    val rarity: BadgeRarity,
    val category: BadgeCategory,
    val criteria: AchievementCriteria,
    val iconUrl: String? = null
)
