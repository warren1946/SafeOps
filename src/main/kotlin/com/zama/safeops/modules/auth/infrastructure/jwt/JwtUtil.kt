/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 * Permission granted for use, modification, and distribution with attribution.
 * No warranty provided.
 */

package com.zama.safeops.modules.auth.infrastructure.jwt

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import org.springframework.stereotype.Component
import java.util.*

@Component
class JwtUtil {

    //todo  THIS_IS_A_DEMO_SECRET_KEY_CHANGE_ME_32_CHARS_MINIMUM
    private val secretKey = Keys.hmacShaKeyFor(
        "THIS_IS_A_DEMO_SECRET_KEY_CHANGE_ME_32_CHARS_MINIMUM".toByteArray()
    )

    private val expirationMs = 1000 * 60 * 60 * 24 // 24 hours

    fun generateToken(userId: Long): String {
        val now = Date()
        val expiry = Date(now.time + expirationMs)

        return Jwts.builder()
            .setSubject(userId.toString())
            .setIssuedAt(now)
            .setExpiration(expiry)
            .signWith(secretKey, SignatureAlgorithm.HS256)
            .compact()
    }

    fun validateToken(token: String): Boolean =
        try {
            getClaims(token)
            true
        } catch (ex: Exception) {
            false
        }

    fun extractUserId(token: String): Long? =
        try {
            getClaims(token).subject.toLong()
        } catch (ex: Exception) {
            null
        }

    private fun getClaims(token: String): Claims =
        Jwts.parserBuilder()
            .setSigningKey(secretKey)
            .build()
            .parseClaimsJws(token)
            .body
}