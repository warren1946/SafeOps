# SafeOps Frontend - Build & Deployment Guide

## Overview

Jetpack Compose Multiplatform frontend with Role-Based Access Control for the SafeOps Mining Safety Platform.

## Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                    Presentation Layer                        │
│  ┌─────────────┐  ┌──────────────┐  ┌──────────────────┐   │
│  │   Screens   │  │   ViewModels │  │   RBAC System    │   │
│  │  (Voyager)  │  │  (ScreenModel)│  │ (Role-Based UI) │   │
│  └──────┬──────┘  └──────┬───────┘  └────────┬─────────┘   │
│         └─────────────────┴───────────────────┘              │
├─────────────────────────────────────────────────────────────┤
│                      Domain Layer                            │
│  ┌──────────────┐  ┌──────────────┐  ┌────────────────┐    │
│  │  Use Cases   │  │    Models    │  │  Repositories  │    │
│  │   (Koin)     │  │   (UserRole) │  │  (Interfaces)  │    │
│  └──────────────┘  └──────────────┘  └────────────────┘    │
├─────────────────────────────────────────────────────────────┤
│                       Data Layer                             │
│  ┌──────────────┐  ┌──────────────┐  ┌────────────────┐    │
│  │  API Client  │  │  Repository  │  │     DTOs       │    │
│  │   (Ktor)     │  │Implementation│  │  (kotlinx.ser) │    │
│  └──────────────┘  └──────────────┘  └────────────────┘    │
└─────────────────────────────────────────────────────────────┘
```

## Project Structure

```
safeops-frontend/
├── composeApp/
│   └── src/commonMain/kotlin/com/zama/safeops/frontend/
│       ├── app/
│       │   └── SafeOpsApp.kt
│       ├── data/
│       │   ├── api/
│       │   │   └── SafeOpsApi.kt          # Updated for RBAC
│       │   ├── model/                      # DTOs
│       │   └── repository/
│       ├── di/
│       │   └── KoinModules.kt
│       ├── domain/
│       │   ├── model/
│       │   │   └── User.kt                # RBAC roles
│       │   └── usecase/
│       ├── presentation/
│       │   ├── components/
│       │   ├── rbac/                      # NEW: RBAC system
│       │   │   └── RoleBasedAccess.kt
│       │   ├── screens/
│       │   │   ├── admin/                 # NEW: Admin screens
│       │   │   │   └── AdminDashboardScreen.kt
│       │   │   ├── auth/
│       │   │   ├── dashboard/
│       │   │   │   └── DashboardScreen.kt # Updated for RBAC
│       │   │   └── ...
│       │   └── theme/
│       │       ├── Theme.kt               # Mining Safety Theme
│       │       └── Type.kt
│       └── config/
│           └── SecurityHeadersConfig.kt   # NEW: Security headers
```

## Features Implemented

### 1. Role-Based Access Control (RBAC)

**User Roles:**

- SUPER_ADMIN - Full system access
- ADMIN - Tenant administrator
- SUPERVISOR - Site supervisor
- OFFICER - Field safety officer
- VIEWER - Read-only access

**RBAC Composables:**

```kotlin
WithRole(UserRole.ADMIN) { }
WithAnyRole(UserRole.ADMIN, UserRole.SUPER_ADMIN) { }
WithAdminPanelAccess { }
WithInspectionCreationPermission { }
WithHazardManagementPermission { }
```

### 2. Role-Based Navigation

Bottom navigation adapts to user role:

- All users: Home, Inspections, Profile
- Non-viewers: Hazards tab
- Admin/Supervisor/Super Admin: Equipment tab

### 3. Theme Colors

**Mining Safety Theme:**

- Primary: Deep Blue (#1E40AF)
- Secondary: Safety Orange (#F97316)
- Success: Safety Green (#22C55E)
- Error: Safety Red (#EF4444)
- Warning: Safety Yellow (#EAB308)

### 4. Screens by Role

| Role        | Screens                                                |
|-------------|--------------------------------------------------------|
| SUPER_ADMIN | Full Admin Dashboard, Tenant Management, System Config |
| ADMIN       | Admin Dashboard, User Management, Reports              |
| SUPERVISOR  | Dashboard, Inspection Approval, Hazard Management      |
| OFFICER     | Mobile Dashboard, Quick Inspection, Hazard Report      |
| VIEWER      | Read-only Dashboard, View Reports                      |

## Building the Application

### Prerequisites

- JDK 17+
- Android Studio Ladybug (2024.2.1) or newer
- Android SDK (API 26+)

### Build Commands

```bash
# Navigate to frontend directory
cd safeops-frontend

# Build for Android
./gradlew :composeApp:installDebug

# Build for Desktop
./gradlew :composeApp:run

# Package Desktop Distribution
./gradlew :composeApp:packageDistributionForCurrentOS
```

## API Configuration

The frontend connects to:

```
Production: https://safeops-1.onrender.com/api
```

Configured in `SafeOpsApi.kt`:

```kotlin
companion object {
    private const val BASE_URL = "https://safeops-1.onrender.com/api"
}
```

## Testing Different Roles

### 1. Register Test Users

```bash
# Register as OFFICER (default)
curl -X POST https://safeops-1.onrender.com/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{"email":"officer@test.com","password":"password123","roles":["OFFICER"]}'

# Register as SUPERVISOR
curl -X POST https://safeops-1.onrender.com/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{"email":"supervisor@test.com","password":"password123","roles":["SUPERVISOR"]}'

# Register as ADMIN (will be filtered to OFFICER due to security fix)
curl -X POST https://safeops-1.onrender.com/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@test.com","password":"password123","roles":["ADMIN"]}'
```

### 2. Login and Test

Login with different credentials to see role-specific UI.

## Security Features

1. **JWT Authentication** - Secure token-based auth
2. **Role Validation** - Server and client-side role checking
3. **Security Headers** - CSP, X-Frame-Options, etc.
4. **Privilege Escalation Protection** - Cannot self-assign admin roles
5. **Method Security** - @PreAuthorize annotations

## File Changes Summary

### New Files (6):

1. `domain/model/User.kt` - Updated with RBAC
2. `presentation/rbac/RoleBasedAccess.kt` - RBAC system
3. `presentation/screens/admin/AdminDashboardScreen.kt` - Admin UI
4. `config/SecurityHeadersConfig.kt` - Security headers
5. `ROLE_BASED_ACCESS_CONTROL.md` - Documentation
6. `FRONTEND_BUILD_GUIDE.md` - This file

### Modified Files (4):

1. `presentation/screens/dashboard/DashboardScreen.kt` - RBAC UI
2. `presentation/screens/auth/AuthViewModel.kt` - UserSession
3. `data/api/SafeOpsApi.kt` - API integration
4. `data/repository/AuthRepository.kt` - Auth handling

## Next Steps

1. **Connect to Real API** - Test with production backend
2. **Add Offline Support** - Realm database integration
3. **Image Capture** - Camera integration for hazard photos
4. **Push Notifications** - Firebase Cloud Messaging
5. **Biometric Auth** - Fingerprint/Face ID login

## Troubleshooting

### Build Issues

```bash
# Clean build
./gradlew clean

# Refresh dependencies
./gradlew --refresh-dependencies
```

### API Connection Issues

Check `SafeOpsApi.kt` BASE_URL configuration.

### Role Not Working

Ensure backend returns roles in login response.

## License

MIT License - See parent project for details.
