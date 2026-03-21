# Role-Based Access Control (RBAC) - SafeOps Frontend

## Overview

The SafeOps frontend implements a comprehensive Role-Based Access Control system that matches the backend authentication system. Users are assigned roles that determine what features they can access and what actions they can perform.

## User Roles

| Role            | Description                                    | Permissions                                                          |
|-----------------|------------------------------------------------|----------------------------------------------------------------------|
| **SUPER_ADMIN** | Platform administrator with full system access | Full access to all features, tenant management, system configuration |
| **ADMIN**       | Tenant administrator                           | Manage users within tenant, view all reports, manage locations       |
| **SUPERVISOR**  | Mine/site supervisor                           | Create/approve inspections, manage hazards, view reports             |
| **OFFICER**     | Field safety officer                           | Create inspections, report hazards, mobile-focused                   |
| **VIEWER**      | Read-only access                               | View dashboards and reports only                                     |

## Role-Based UI Components

### 1. Composable Functions for Role-Based Visibility

```kotlin
// Show content only for specific roles
WithRole(UserRole.ADMIN) {
    AdminButton()
}

// Show content for any of the specified roles
WithAnyRole(UserRole.ADMIN, UserRole.SUPER_ADMIN) {
    UserManagementSection()
}

// Show content based on permissions
WithAdminPanelAccess {
    AdminPanelLink()
}

WithInspectionCreationPermission {
    CreateInspectionButton()
}

WithHazardManagementPermission {
    ReportHazardButton()
}

WithUserManagementPermission {
    UserList()
}
```

### 2. Role-Based Content Selection

```kotlin
RoleBasedContent(
    superAdminContent = { SuperAdminDashboard() },
    adminContent = { AdminDashboard() },
    supervisorContent = { SupervisorDashboard() },
    officerContent = { OfficerDashboard() },
    viewerContent = { ViewerDashboard() },
    defaultContent = { GenericDashboard() }
)
```

### 3. Checking Permissions Programmatically

```kotlin
if (canPerformAction(UserAction.CREATE_INSPECTION)) {
    // Show create inspection button
}

if (canPerformAction(UserAction.MANAGE_USERS)) {
    // Show user management
}
```

## UserSession - Global User State

```kotlin
// Get current user anywhere
val user = UserSession.currentUser

// Check if authenticated
if (UserSession.isAuthenticated()) {
    // Show authenticated content
}

// Get primary role
val role = UserSession.currentUser.primaryRole

// Check specific role
if (UserSession.currentUser.hasRole(UserRole.ADMIN)) {
    // Admin-only code
}
```

## Screens by Role

### Super Admin Screens

- Full Admin Dashboard
- Tenant Management
- System Configuration
- Audit Logs
- User Management (all tenants)

### Admin Screens

- Admin Dashboard
- User Management (own tenant)
- Location Management
- Reports & Analytics
- Settings

### Supervisor Screens

- Dashboard with team overview
- Inspection Management (create, approve)
- Hazard Management (assign, track)
- Equipment Status
- Reports

### Officer Screens

- Mobile-first Dashboard
- Quick Inspection Creation
- Hazard Reporting
- My Tasks
- Offline-capable forms

### Viewer Screens

- Read-only Dashboard
- Safety Score View
- Inspection History (view only)
- Hazard Status (view only)
- Reports (view only)

## Navigation

The bottom navigation adapts based on user role:

```kotlin
NavigationBar {
    // Home - All users
    NavigationBarItem(...)

    // Inspections - All users
    NavigationBarItem(...)

    // Hazards - All except VIEWER
    if (currentUser.primaryRole != UserRole.VIEWER) {
        NavigationBarItem(...)
    }

    // Equipment - ADMIN, SUPERVISOR, SUPER_ADMIN only
    if (currentUser.primaryRole.canManageEquipment()) {
        NavigationBarItem(...)
    }

    // Profile - All users
    NavigationBarItem(...)
}
```

## Floating Action Button (FAB)

Role-specific FABs:

- **Admin**: "New" with full creation menu
- **Supervisor**: "Inspection" quick create
- **Officer**: Hazard report button
- **Viewer**: No FAB

## Security Headers

All screens include security headers:

- X-Content-Type-Options: nosniff
- X-Frame-Options: DENY
- Content-Security-Policy
- Referrer-Policy
- Permissions-Policy

## API Integration

All API calls include:

- Authorization: Bearer {token}
- X-Tenant-ID: {tenantId}
- X-Tenant-Slug: {tenantSlug}

## Usage Example

```kotlin
@Composable
fun MyScreen() {
    val currentUser = LocalCurrentUser.current

    Column {
        // Show for all users
        Text("Welcome, ${currentUser.displayName}")

        // Admin only
        WithAdminPanelAccess {
            Button(onClick = { /* Navigate to admin */ }) {
                Text("Admin Panel")
            }
        }

        // Officers and above
        WithInspectionCreationPermission {
            Button(onClick = { /* Create inspection */ }) {
                Text("New Inspection")
            }
        }

        // Specific roles
        WithAnyRole(UserRole.SUPERVISOR, UserRole.ADMIN) {
            Button(onClick = { /* Approve inspection */ }) {
                Text("Approve")
            }
        }
    }
}
```

## Testing Different Roles

To test different roles, login with different user credentials:

1. **Super Admin**: Full access to everything
2. **Admin**: Tenant-level administration
3. **Supervisor**: Can approve inspections
4. **Officer**: Can create inspections and report hazards
5. **Viewer**: Read-only access

## Build & Run

```bash
# Android
./gradlew :composeApp:installDebug

# Desktop
./gradlew :composeApp:run
```

## Files Added/Modified

### New Files:

- `domain/model/User.kt` - Updated with RBAC roles
- `presentation/rbac/RoleBasedAccess.kt` - RBAC composables
- `presentation/screens/admin/AdminDashboardScreen.kt` - Admin UI
- `config/SecurityHeadersConfig.kt` - Security headers

### Modified Files:

- `presentation/screens/dashboard/DashboardScreen.kt` - Role-based UI
- `presentation/screens/auth/AuthViewModel.kt` - UserSession integration
- `data/api/SafeOpsApi.kt` - API updates

## Future Enhancements

1. **Role Switching**: Allow users with multiple roles to switch
2. **Permission Granularity**: More fine-grained permissions
3. **Role Requests**: Users can request role upgrades
4. **Audit Trail**: Log all role-based access
