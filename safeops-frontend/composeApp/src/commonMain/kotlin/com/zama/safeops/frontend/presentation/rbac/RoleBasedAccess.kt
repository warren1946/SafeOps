package com.zama.safeops.frontend.presentation.rbac

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.zama.safeops.frontend.domain.model.User
import com.zama.safeops.frontend.domain.model.UserRole
import com.zama.safeops.frontend.domain.model.canAccessAdminPanel
import com.zama.safeops.frontend.domain.model.canApproveInspections
import com.zama.safeops.frontend.domain.model.canCreateInspections
import com.zama.safeops.frontend.domain.model.canDeleteData
import com.zama.safeops.frontend.domain.model.canManageEquipment
import com.zama.safeops.frontend.domain.model.canManageHazards
import com.zama.safeops.frontend.domain.model.canManageTenants
import com.zama.safeops.frontend.domain.model.canManageUsers
import com.zama.safeops.frontend.domain.model.canViewReports

/**
 * CompositionLocal for providing the current user throughout the app
 */
val LocalCurrentUser: ProvidableCompositionLocal<User> = compositionLocalOf { User.guest() }

/**
 * Global user session holder
 */
object UserSession {
    var currentUser: User by mutableStateOf(User.guest())
        private set

    fun login(user: User) {
        currentUser = user
    }

    fun logout() {
        currentUser = User.guest()
    }

    fun isAuthenticated(): Boolean = currentUser.id > 0 && currentUser.enabled
}

/**
 * Provides the current user to the composition tree
 */
@Composable
fun ProvideUser(user: User, content: @Composable () -> Unit) {
    CompositionLocalProvider(LocalCurrentUser provides user) {
        content()
    }
}

/**
 * Only renders content if the current user has the specified role
 */
@Composable
fun WithRole(
    role: UserRole,
    content: @Composable () -> Unit
) {
    val user = LocalCurrentUser.current
    if (user.hasRole(role)) {
        content()
    }
}

/**
 * Only renders content if the current user has any of the specified roles
 */
@Composable
fun WithAnyRole(
    vararg roles: UserRole,
    content: @Composable () -> Unit
) {
    val user = LocalCurrentUser.current
    if (user.hasAnyRole(*roles)) {
        content()
    }
}

/**
 * Only renders content if the current user has permission to manage users
 */
@Composable
fun WithUserManagementPermission(content: @Composable () -> Unit) {
    val user = LocalCurrentUser.current
    if (user.primaryRole.canManageUsers()) {
        content()
    }
}

/**
 * Only renders content if the current user has permission to access admin panel
 */
@Composable
fun WithAdminPanelAccess(content: @Composable () -> Unit) {
    val user = LocalCurrentUser.current
    if (user.primaryRole.canAccessAdminPanel()) {
        content()
    }
}

/**
 * Only renders content if the current user has permission to create inspections
 */
@Composable
fun WithInspectionCreationPermission(content: @Composable () -> Unit) {
    val user = LocalCurrentUser.current
    if (user.primaryRole.canCreateInspections()) {
        content()
    }
}

/**
 * Only renders content if the current user has permission to approve inspections
 */
@Composable
fun WithInspectionApprovalPermission(content: @Composable () -> Unit) {
    val user = LocalCurrentUser.current
    if (user.primaryRole.canApproveInspections()) {
        content()
    }
}

/**
 * Only renders content if the current user has permission to manage hazards
 */
@Composable
fun WithHazardManagementPermission(content: @Composable () -> Unit) {
    val user = LocalCurrentUser.current
    if (user.primaryRole.canManageHazards()) {
        content()
    }
}

/**
 * Only renders content if the current user has permission to view reports
 */
@Composable
fun WithReportViewPermission(content: @Composable () -> Unit) {
    val user = LocalCurrentUser.current
    if (user.primaryRole.canViewReports()) {
        content()
    }
}

/**
 * Only renders content if the current user has permission to manage tenants
 */
@Composable
fun WithTenantManagementPermission(content: @Composable () -> Unit) {
    val user = LocalCurrentUser.current
    if (user.primaryRole.canManageTenants()) {
        content()
    }
}

/**
 * Only renders content if the current user has permission to delete data
 */
@Composable
fun WithDeletePermission(content: @Composable () -> Unit) {
    val user = LocalCurrentUser.current
    if (user.primaryRole.canDeleteData()) {
        content()
    }
}

/**
 * Only renders content if the current user has permission to manage equipment
 */
@Composable
fun WithEquipmentManagementPermission(content: @Composable () -> Unit) {
    val user = LocalCurrentUser.current
    if (user.primaryRole.canManageEquipment()) {
        content()
    }
}

/**
 * Renders different content based on the user's role
 */
@Composable
fun <T> RoleBasedContent(
    superAdminContent: @Composable (() -> Unit)? = null,
    adminContent: @Composable (() -> Unit)? = null,
    supervisorContent: @Composable (() -> Unit)? = null,
    officerContent: @Composable (() -> Unit)? = null,
    viewerContent: @Composable (() -> Unit)? = null,
    defaultContent: @Composable () -> Unit = {}
) {
    val user = LocalCurrentUser.current
    val role = user.primaryRole

    when (role) {
        UserRole.SUPER_ADMIN -> superAdminContent?.invoke() ?: adminContent?.invoke() ?: defaultContent()
        UserRole.ADMIN -> adminContent?.invoke() ?: defaultContent()
        UserRole.SUPERVISOR -> supervisorContent?.invoke() ?: officerContent?.invoke() ?: defaultContent()
        UserRole.OFFICER -> officerContent?.invoke() ?: defaultContent()
        UserRole.VIEWER -> viewerContent?.invoke() ?: defaultContent()
    }
}

/**
 * Returns true if the current user can perform the action
 */
fun canPerformAction(action: UserAction): Boolean {
    val user = UserSession.currentUser
    return when (action) {
        UserAction.CREATE_INSPECTION -> user.primaryRole.canCreateInspections()
        UserAction.APPROVE_INSPECTION -> user.primaryRole.canApproveInspections()
        UserAction.CREATE_HAZARD -> user.primaryRole.canManageHazards()
        UserAction.MANAGE_USERS -> user.primaryRole.canManageUsers()
        UserAction.MANAGE_TENANTS -> user.primaryRole.canManageTenants()
        UserAction.VIEW_REPORTS -> user.primaryRole.canViewReports()
        UserAction.DELETE_DATA -> user.primaryRole.canDeleteData()
        UserAction.MANAGE_EQUIPMENT -> user.primaryRole.canManageEquipment()
        UserAction.ACCESS_ADMIN_PANEL -> user.primaryRole.canAccessAdminPanel()
    }
}

/**
 * Actions that can be performed in the app
 */
enum class UserAction {
    CREATE_INSPECTION,
    APPROVE_INSPECTION,
    CREATE_HAZARD,
    MANAGE_USERS,
    MANAGE_TENANTS,
    VIEW_REPORTS,
    DELETE_DATA,
    MANAGE_EQUIPMENT,
    ACCESS_ADMIN_PANEL
}
