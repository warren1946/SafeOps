/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 */

package com.zama.safeops.modules.engagement.infrastructure.adapters

import com.zama.safeops.modules.engagement.application.ports.GamificationPort
import com.zama.safeops.modules.engagement.domain.model.*
import org.springframework.stereotype.Component

/**
 * Stub implementation of GamificationPort.
 */
@Component
class StubGamificationAdapter : GamificationPort {

    override fun getScorecard(tenantId: Long, userId: Long): SafetyScorecard? = null

    override fun saveScorecard(scorecard: SafetyScorecard): SafetyScorecard = scorecard

    override fun getLeaderboard(tenantId: Long, type: LeaderboardType, timeFrame: TimeFrame, limit: Int): List<LeaderboardEntry> =
        emptyList()

    override fun recordTransaction(transaction: PointsTransaction) {}

    override fun addPoints(tenantId: Long, userId: Long, points: Int) {}

    override fun updateStreak(tenantId: Long, userId: Long, streak: StreakInfo) {}

    override fun getUserBadges(tenantId: Long, userId: Long): List<Badge> = emptyList()

    override fun addBadge(tenantId: Long, userId: Long, badge: Badge) {}

    override fun saveChallenge(challenge: SafetyChallenge): SafetyChallenge = challenge

    override fun addParticipant(challengeId: String, userId: Long): SafetyChallenge {
        throw NotImplementedError("Stub implementation")
    }

    override fun getActiveChallenges(tenantId: Long): List<SafetyChallenge> = emptyList()

    override fun getCompletedChallenges(): List<SafetyChallenge> = emptyList()

    override fun getCompletedInspectionsCount(tenantId: Long, userId: Long, timeFrame: TimeFrame?): Int = 0

    override fun getPerfectInspectionsCount(tenantId: Long, userId: Long, timeFrame: TimeFrame?): Int = 0

    override fun getReportedHazardsCount(tenantId: Long, userId: Long, timeFrame: TimeFrame?): Int = 0

    override fun getCriticalHazardsCount(tenantId: Long, userId: Long, timeFrame: TimeFrame?): Int = 0
}
