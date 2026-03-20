/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 */

package com.zama.safeops.config

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Component
import org.springframework.web.servlet.HandlerInterceptor
import java.time.Instant
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger

/**
 * Simple rate limiting configuration.
 *
 * Limits:
 * - Authentication endpoints: 10 requests per minute (prevent brute force)
 * - General API: 100 requests per minute per user
 * - Heavy operations (AI analysis, reports): 10 requests per minute
 * - Public endpoints: 60 requests per minute per IP
 */
class RateLimitConfig {
    companion object {
        const val AUTH_LIMIT = 10
        const val GENERAL_LIMIT = 100
        const val HEAVY_LIMIT = 10
        const val PUBLIC_LIMIT = 60

        const val WINDOW_SECONDS = 60L
    }
}

/**
 * Rate limit bucket for tracking requests.
 */
data class RateLimitBucket(
    val count: AtomicInteger = AtomicInteger(0),
    val windowStart: Long = Instant.now().epochSecond
) {
    fun isExpired(windowSeconds: Long): Boolean {
        return Instant.now().epochSecond - windowStart > windowSeconds
    }

    fun tryConsume(): Boolean {
        return count.incrementAndGet() <= RateLimitConfig.GENERAL_LIMIT
    }

    fun tryConsume(limit: Int): Boolean {
        return count.incrementAndGet() <= limit
    }
}

/**
 * Rate limiting interceptor - enforces rate limits per user/IP
 */
@Component
class RateLimitingInterceptor : HandlerInterceptor {

    private val buckets = ConcurrentHashMap<String, RateLimitBucket>()

    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        val clientId = extractClientId(request)
        val bucketType = determineBucketType(request)
        val limit = getLimitForType(bucketType)
        val bucketKey = "$clientId:$bucketType"

        // Get or create bucket
        var bucket = buckets[bucketKey]

        // Check if bucket is expired and recreate if needed
        if (bucket == null || bucket.isExpired(RateLimitConfig.WINDOW_SECONDS)) {
            bucket = RateLimitBucket()
            buckets[bucketKey] = bucket
        }

        // Try to consume a token
        val remaining = limit - bucket.count.get()

        return if (remaining > 0) {
            bucket.count.incrementAndGet()
            response.addHeader("X-Rate-Limit-Limit", limit.toString())
            response.addHeader("X-Rate-Limit-Remaining", (remaining - 1).toString())
            response.addHeader("X-Rate-Limit-Reset", (bucket.windowStart + RateLimitConfig.WINDOW_SECONDS).toString())
            true
        } else {
            response.status = 429 // Too Many Requests
            response.addHeader("X-Rate-Limit-Limit", limit.toString())
            response.addHeader("X-Rate-Limit-Remaining", "0")
            response.addHeader("X-Rate-Limit-Retry-After", RateLimitConfig.WINDOW_SECONDS.toString())
            response.contentType = "application/json"
            response.writer.write(
                """{"success":false,"error":{"code":"RATE_LIMIT_EXCEEDED","message":"Too many requests. Please try again later."},"timestamp":"${Instant.now()}"}"""
            )
            false
        }
    }

    private fun extractClientId(request: HttpServletRequest): String {
        // Try to get user ID from authentication, fall back to IP address
        val userId = request.getAttribute("userId") as? String
        return userId ?: getClientIP(request)
    }

    private fun getClientIP(request: HttpServletRequest): String {
        var xfHeader = request.getHeader("X-Forwarded-For")
        if (xfHeader == null) {
            return request.remoteAddr
        }
        return xfHeader.split(",")[0].trim()
    }

    private fun determineBucketType(request: HttpServletRequest): BucketType {
        val path = request.requestURI
        return when {
            path.contains("/auth/") -> BucketType.AUTH
            path.contains("/ai/") || path.contains("/reports/generate") || path.contains("/export") -> BucketType.HEAVY
            path.contains("/public/") || path.contains("/health") || path.contains("/swagger-ui") || path.contains("/v3/api-docs") -> BucketType.PUBLIC
            else -> BucketType.GENERAL
        }
    }

    private fun getLimitForType(type: BucketType): Int {
        return when (type) {
            BucketType.AUTH -> RateLimitConfig.AUTH_LIMIT
            BucketType.HEAVY -> RateLimitConfig.HEAVY_LIMIT
            BucketType.PUBLIC -> RateLimitConfig.PUBLIC_LIMIT
            BucketType.GENERAL -> RateLimitConfig.GENERAL_LIMIT
        }
    }

    enum class BucketType {
        AUTH,
        GENERAL,
        HEAVY,
        PUBLIC
    }
}

/**
 * Annotation for marking endpoints with custom rate limits
 */
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class RateLimit(
    val requestsPerMinute: Int = 100,
    val burstCapacity: Int = 10
)
