/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 */

package com.zama.safeops.modules.engagement.application.ports

import com.zama.safeops.modules.engagement.domain.model.*

/**
 * Port for gamification data persistence.
 */
interface GamificationPort {
    fun getScorecard(tenantId: Long, userId: Long): SafetyScorecard?
    fun saveScorecard(scorecard: SafetyScorecard): SafetyScorecard
    fun getLeaderboard(tenantId: Long, type: LeaderboardType, timeFrame: TimeFrame, limit: Int): List<LeaderboardEntry>
    fun recordTransaction(transaction: PointsTransaction)
    fun addPoints(tenantId: Long, userId: Long, points: Int)
    fun updateStreak(tenantId: Long, userId: Long, streak: StreakInfo)
    fun getUserBadges(tenantId: Long, userId: Long): List<Badge>
    fun addBadge(tenantId: Long, userId: Long, badge: Badge)
    fun saveChallenge(challenge: SafetyChallenge): SafetyChallenge
    fun addParticipant(challengeId: String, userId: Long): SafetyChallenge
    fun getActiveChallenges(tenantId: Long): List<SafetyChallenge>
    fun getCompletedChallenges(): List<SafetyChallenge>
    fun getCompletedInspectionsCount(tenantId: Long, userId: Long, timeFrame: TimeFrame?): Int
    fun getPerfectInspectionsCount(tenantId: Long, userId: Long, timeFrame: TimeFrame?): Int
    fun getReportedHazardsCount(tenantId: Long, userId: Long, timeFrame: TimeFrame?): Int
    fun getCriticalHazardsCount(tenantId: Long, userId: Long, timeFrame: TimeFrame?): Int
}
