# SafeOps Frontend - Project Structure

## Overview

A Compose Multiplatform project targeting **Android** and **Desktop (JVM)** for the SafeOps Mining Safety Platform.

## Directory Structure

```
safeops-frontend/
├── composeApp/
│   ├── src/
│   │   ├── commonMain/kotlin/com/zama/safeops/frontend/
│   │   │   ├── app/
│   │   │   │   └── SafeOpsApp.kt              # Main app entry
│   │   │   ├── data/
│   │   │   │   ├── api/
│   │   │   │   │   └── SafeOpsApi.kt          # Ktor API client
│   │   │   │   ├── model/                      # DTOs
│   │   │   │   └── repository/
│   │   │   │       ├── AuthRepository.kt
│   │   │   │       ├── HazardRepository.kt
│   │   │   │       ├── InspectionRepository.kt
│   │   │   │       └── SafetyScoreRepository.kt
│   │   │   ├── di/
│   │   │   │   └── KoinModules.kt             # DI configuration
│   │   │   ├── domain/
│   │   │   │   ├── model/
│   │   │   │   │   ├── Hazard.kt
│   │   │   │   │   ├── Inspection.kt
│   │   │   │   │   ├── SafetyScore.kt
│   │   │   │   │   └── User.kt
│   │   │   │   └── usecase/
│   │   │   │       ├── auth/
│   │   │   │       │   ├── GetCurrentUserUseCase.kt
│   │   │   │       │   ├── LoginUseCase.kt
│   │   │   │       │   └── LogoutUseCase.kt
│   │   │   │       ├── hazard/
│   │   │   │       │   ├── CreateHazardUseCase.kt
│   │   │   │       │   └── GetHazardsUseCase.kt
│   │   │   │       ├── inspection/
│   │   │   │       │   └── GetInspectionsUseCase.kt
│   │   │   │       └── safety/
│   │   │   │           └── GetSafetyScoreUseCase.kt
│   │   │   └── presentation/
│   │   │       ├── components/                 # Reusable UI components
│   │   │       ├── screens/
│   │   │       │   ├── auth/
│   │   │       │   │   ├── AuthViewModel.kt
│   │   │       │   │   └── LoginScreen.kt
│   │   │       │   ├── dashboard/
│   │   │       │   │   ├── DashboardScreen.kt
│   │   │       │   │   └── DashboardViewModel.kt
│   │   │       │   ├── hazards/
│   │   │       │   │   └── HazardsViewModel.kt
│   │   │       │   ├── inspections/
│   │   │       │   │   └── InspectionsViewModel.kt
│   │   │       │   └── splash/
│   │   │       │       └── SplashScreen.kt
│   │   │       └── theme/
│   │   │           ├── Theme.kt
│   │   │           └── Type.kt
│   │   ├── androidMain/
│   │   │   ├── AndroidManifest.xml
│   │   │   └── kotlin/com/zama/safeops/frontend/
│   │   │       ├── MainActivity.kt
│   │   │       └── SafeOpsApplication.kt
│   │   └── desktopMain/kotlin/com/zama/safeops/frontend/
│   │       └── Main.kt
│   └── build.gradle.kts
├── shared/
│   └── src/
│       ├── commonMain/kotlin/com/zama/safeops/frontend/shared/
│       │   └── Platform.kt
│       ├── androidMain/kotlin/com/zama/safeops/frontend/shared/
│       │   └── Platform.android.kt
│       └── desktopMain/kotlin/com/zama/safeops/frontend/shared/
│           └── Platform.desktop.kt
├── gradle/
│   ├── libs.versions.toml                      # Version catalog
│   └── wrapper/
│       ├── gradle-wrapper.jar
│       └── gradle-wrapper.properties
├── README.md
└── settings.gradle.kts
```

## Architecture

### Clean Architecture Layers

```
┌─────────────────────────────────────────┐
│  Presentation Layer (Compose + Voyager) │
│  - Screens, ViewModels, UI Components   │
├─────────────────────────────────────────┤
│  Domain Layer                           │
│  - Use Cases, Domain Models             │
├─────────────────────────────────────────┤
│  Data Layer                             │
│  - Repositories, API Client, DTOs       │
└─────────────────────────────────────────┘
```

### Key Dependencies

| Category          | Libraries                        |
|-------------------|----------------------------------|
| **UI**            | Compose Multiplatform, Material3 |
| **Navigation**    | Voyager                          |
| **DI**            | Koin                             |
| **Networking**    | Ktor Client                      |
| **Serialization** | kotlinx.serialization            |
| **Date/Time**     | kotlinx.datetime                 |
| **Database**      | Realm                            |
| **Logging**       | Napier                           |
| **Images**        | Coil 3                           |

## Build Commands

```bash
# Android
./gradlew :composeApp:installDebug

# Desktop
./gradlew :composeApp:run

# Package Desktop
./gradlew :composeApp:packageDistributionForCurrentOS
```

## API Configuration

Configured in `composeApp/build.gradle.kts` via BuildKonfig:

| Environment | Base URL                    |
|-------------|-----------------------------|
| dev         | http://localhost:8080       |
| staging     | https://staging.safeops.com |
| prod        | https://api.safeops.com     |

## Screens Implemented

1. **SplashScreen** - App initialization with logo animation
2. **LoginScreen** - Email/password authentication
3. **DashboardScreen** - Main dashboard with:
    - Safety Score card
    - Recent inspections list
    - Open hazards list
    - Bottom navigation (Home, Inspections, Hazards, Equipment)

## Next Steps

1. Complete remaining screens:
    - Inspections list with filters
    - Hazard reporting form
    - Equipment management
    - User profile

2. Add offline support with Realm

3. Implement image capture for hazards

4. Add push notifications

5. Add biometric authentication
