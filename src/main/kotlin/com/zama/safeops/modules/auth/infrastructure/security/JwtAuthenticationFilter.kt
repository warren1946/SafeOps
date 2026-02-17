/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 * Permission granted for use, modification, and distribution with attribution.
 * No warranty provided.
 */

package com.zama.safeops.modules.auth.infrastructure.security

import com.zama.safeops.modules.auth.application.ports.UserPort
import com.zama.safeops.modules.auth.application.services.TokenService
import com.zama.safeops.modules.auth.domain.valueobjects.UserId
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtAuthenticationFilter(private val tokenService: TokenService, private val userPort: UserPort) : OncePerRequestFilter() {

    override fun shouldNotFilter(request: HttpServletRequest): Boolean {
        println("shouldNotFilter: ${request.servletPath}")
        return request.servletPath.startsWith("/api/auth/")

    }

    override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, filterChain: FilterChain) {
        val header = request.getHeader("Authorization")

        if (header == null || !header.startsWith("Bearer ")) {
            filterChain.doFilter(request, response)
            return
        }

        val token = header.removePrefix("Bearer ").trim()

        try {
            val userId = tokenService.parseUserIdFromAccessToken(token)
            val user = userPort.findById(UserId(userId))

            if (user != null && SecurityContextHolder.getContext().authentication == null) {
                val authorities = user.roles
                    .map { SimpleGrantedAuthority(it.name.value) }

                val auth = UsernamePasswordAuthenticationToken(
                    user.id.value,
                    null,
                    authorities
                ).apply {
                    details = WebAuthenticationDetailsSource().buildDetails(request)
                }

                SecurityContextHolder.getContext().authentication = auth
            }
        } catch (ex: Exception) {
            // Optionally log or handle token errors; we just fall through as unauthenticated
        }

        filterChain.doFilter(request, response)
    }
}