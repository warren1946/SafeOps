/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 * Permission granted for use, modification, and distribution with attribution.
 * No warranty provided.
 */

package com.zama.safeops.modules.auth.infrastructure.security

import com.zama.safeops.modules.auth.application.services.TokenService
import com.zama.safeops.modules.auth.domain.exceptions.ExpiredTokenException
import com.zama.safeops.modules.auth.domain.exceptions.InvalidTokenException
import com.zama.safeops.modules.auth.domain.model.User
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.JwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.util.*

@Component
class JwtTokenService(
    @Value("\${app.jwt.access-secret}") private val accessSecret: String,
    @Value("\${app.jwt.refresh-secret}") private val refreshSecret: String,
    @Value("\${app.jwt.access-expiration-ms:900000}") private val accessExpirationMs: Long,   // 15 min
    @Value("\${app.jwt.refresh-expiration-ms:1209600000}") private val refreshExpirationMs: Long // 14 days
) : TokenService {

    override fun generateAccessToken(user: User): String {
        val now = Date()
        val expiry = Date(now.time + accessExpirationMs)
        val key = Keys.hmacShaKeyFor(accessSecret.toByteArray())

        return Jwts.builder()
            .setSubject(user.id?.value.toString())
            .claim("email", user.email.value)
            .claim("roles", user.roles.map { it.name.value })
            .setIssuedAt(now)
            .setExpiration(expiry)
            .signWith(key, SignatureAlgorithm.HS256)
            .compact()
    }

    override fun generateRefreshToken(user: User): String {
        val now = Date()
        val expiry = Date(now.time + refreshExpirationMs)
        val key = Keys.hmacShaKeyFor(refreshSecret.toByteArray())

        return Jwts.builder()
            .setSubject(user.id?.value.toString())
            .setIssuedAt(now)
            .setExpiration(expiry)
            .signWith(key, SignatureAlgorithm.HS256)
            .compact()
    }

    override fun parseUserIdFromAccessToken(token: String): Long {
        val key = Keys.hmacShaKeyFor(accessSecret.toByteArray())
        val claims = Jwts.parserBuilder()
            .setSigningKey(key)
            .build()
            .parseClaimsJws(token)
            .body

        return claims.subject.toLong()
    }

    override fun parseUserIdFromRefreshToken(token: String): Long {
        try {

            val key = Keys.hmacShaKeyFor(refreshSecret.toByteArray())
            val claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .body

            return claims.subject.toLong()
        } catch (ex: ExpiredJwtException) {
            throw ExpiredTokenException()
        } catch (ex: JwtException) {
            throw InvalidTokenException("Invalid token")
        }
    }
}