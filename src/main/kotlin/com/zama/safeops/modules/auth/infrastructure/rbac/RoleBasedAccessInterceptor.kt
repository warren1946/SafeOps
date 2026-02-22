/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 * Permission granted for use, modification, and distribution with attribution.
 * No warranty provided.
 */

package com.zama.safeops.modules.auth.infrastructure.rbac

import com.zama.safeops.modules.auth.domain.model.User
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Component
import org.springframework.web.method.HandlerMethod
import org.springframework.web.servlet.HandlerInterceptor

@Component
class RoleBasedAccessInterceptor(
    private val currentUserProvider: CurrentUserProvider
) : HandlerInterceptor {

    override fun preHandle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any
    ): Boolean {
        if (handler !is HandlerMethod) return true

        val annotation = findRequiresRoleAnnotation(handler) ?: return true

        val requiredRoles = annotation.value.toSet()
        if (requiredRoles.isEmpty()) return true

        val user = currentUserProvider.getCurrentUser()
            ?: return forbidden(response, "Authentication required")

        if (!hasAnyRequiredRole(user, requiredRoles)) {
            return forbidden(response, "Forbidden: missing required role")
        }

        return true
    }

    private fun findRequiresRoleAnnotation(handler: HandlerMethod): RequiresRole? {
        return handler.getMethodAnnotation(RequiresRole::class.java)
            ?: handler.beanType.getAnnotation(RequiresRole::class.java)
    }

    private fun hasAnyRequiredRole(user: User, requiredRoles: Set<String>): Boolean {
        val userRoleNames = user.roles.map { it.name.value }.toSet()
        return userRoleNames.any { it in requiredRoles }
    }

    private fun forbidden(response: HttpServletResponse, message: String): Boolean {
        response.status = HttpServletResponse.SC_FORBIDDEN
        response.contentType = "application/json"
        response.writer.write("""{"success":false,"message":"$message","data":null}""")
        return false
    }
}