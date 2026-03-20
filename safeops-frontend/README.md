# SafeOps Frontend

A **Compose Multiplatform** frontend for the SafeOps Mining Safety Platform. Built with Kotlin Multiplatform (KMP) supporting Android and Desktop (JVM) targets.

## Features

- **Cross-platform UI**: Shared business logic and UI components across Android and Desktop
- **Modern Architecture**: MVVM with Voyager navigation, Koin DI, and Ktor networking
- **Material Design 3**: Consistent, accessible UI following Material Design guidelines
- **Offline Support**: Realm database for local data persistence
- **Real-time Updates**: WebSocket support for live notifications

## Project Structure

```
safeops-frontend/
├── composeApp/
│   ├── src/
│   │   ├── commonMain/kotlin/      # Shared code (UI, ViewModels, Domain)
│   │   ├── androidMain/kotlin/     # Android-specific code
│   │   └── desktopMain/kotlin/     # Desktop-specific code
│   └── build.gradle.kts
├── shared/                          # Shared non-UI code
│   ├── src/
│   │   ├── commonMain/kotlin/
│   │   ├── androidMain/kotlin/
│   │   └── desktopMain/kotlin/
│   └── build.gradle.kts
├── gradle/
│   └── libs.versions.toml          # Version catalog
├── settings.gradle.kts
└── README.md
```

## Architecture

### Layers

1. **Presentation Layer**
    - Screens (Voyager Screen interface)
    - ViewModels (Voyager ScreenModel)
    - UI Components (Compose)

2. **Domain Layer**
    - Use Cases
    - Domain Models
    - Repository Interfaces

3. **Data Layer**
    - API Client (Ktor)
    - Repository Implementations
    - DTOs/Data Models

### Key Technologies

| Component            | Technology                    |
|----------------------|-------------------------------|
| UI Framework         | Jetpack Compose Multiplatform |
| Navigation           | Voyager                       |
| Dependency Injection | Koin                          |
| Networking           | Ktor Client                   |
| Serialization        | kotlinx.serialization         |
| Date/Time            | kotlinx.datetime              |
| Local DB             | Realm Kotlin                  |
| Logging              | Napier                        |
| Image Loading        | Coil 3                        |

## Getting Started

### Prerequisites

- JDK 17+
- Android Studio Ladybug (2024.2.1) or newer
- Android SDK (API 26+)

### Setup

1. Clone the repository:

```bash
git clone <repo-url>
cd safeops-frontend
```

2. Open in Android Studio or IntelliJ IDEA

3. Sync Gradle project

### Running

#### Android

```bash
./gradlew :composeApp:installDebug
```

#### Desktop

```bash
./gradlew :composeApp:run
```

#### Desktop Distribution

```bash
./gradlew :composeApp:packageDistributionForCurrentOS
```

## API Configuration

The API base URL is configured through BuildKonfig for different build types:

- **dev**: `http://localhost:8080`
- **staging**: `https://staging.safeops.com`
- **prod**: `https://api.safeops.com`

Edit `composeApp/build.gradle.kts` to change these values.

## Screens

| Screen      | Description                                                        |
|-------------|--------------------------------------------------------------------|
| Splash      | App initialization and auth check                                  |
| Login       | User authentication                                                |
| Dashboard   | Main dashboard with safety score, recent inspections, open hazards |
| Inspections | List and manage inspections                                        |
| Hazards     | Report and track hazards                                           |
| Equipment   | Equipment status and maintenance                                   |

## Backend Integration

This frontend connects to the SafeOps Spring Boot backend at `http://localhost:8080`.

See the main project `../bruno/SafeOps/` for API collection.

## License

MIT License - See parent project for details.
