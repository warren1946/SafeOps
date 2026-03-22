# SafeOps Architecture Documentation

## Overview
This project follows **Clean Architecture** with **MVVM (Model-View-ViewModel)** pattern, adhering to **SOLID principles**.

## Architecture Layers

```
┌─────────────────────────────────────────────────────────────┐
│                    PRESENTATION LAYER                       │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐     │
│  │  UI Screens  │  │  ViewModels  │  │    State     │     │
│  │  (Compose)   │  │   (MVVM)     │  │   (Flow)     │     │
│  └──────────────┘  └──────────────┘  └──────────────┘     │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                      DOMAIN LAYER                           │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐     │
│  │    Models    │  │ Repositories │  │  Use Cases   │     │
│  │   (Domain)   │  │ (Interfaces) │  │  (Business)  │     │
│  └──────────────┘  └──────────────┘  └──────────────┘     │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                       DATA LAYER                            │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐     │
│  │     DTOs     │  │   Services   │  │ Repositories │     │
│  │   (Remote)   │  │   (Ktor)     │  │     Impl     │     │
│  └──────────────┘  └──────────────┘  └──────────────┘     │
└─────────────────────────────────────────────────────────────┘
```

## Layer Details

### 1. Presentation Layer
**Location:** `org.example.project.presentation`

- **ViewModels**: Handle UI logic, expose StateFlow for UI state
- **Screens**: Compose UI components
- **State Management**: Using Kotlin Flow and StateFlow

**ViewModels:**
- `LoginViewModel` - Authentication logic
- `RegisterViewModel` - Registration flow
- `DashboardViewModel` - Dashboard statistics
- `InspectionsViewModel` - Inspections list management
- `HazardsViewModel` - Hazards management
- `UsersViewModel` - User management

### 2. Domain Layer
**Location:** `org.example.project.domain`

- **Models**: Pure Kotlin data classes (independent of frameworks)
- **Repository Interfaces**: Define contracts for data access
- **Use Cases**: Single-responsibility business logic

**Models:**
- `User`, `UserRole`, `UserSession`
- `Inspection`, `InspectionStatus`, `InspectionTargetType`
- `Hazard`, `HazardSeverity`, `HazardStatus`
- `DashboardStatistics`, `Activity`, `ChartData`
- `AuthTokens`

**Use Cases:**
- `LoginUseCase` - Input validation + login
- `RegisterUseCase` - Registration flow
- `GetDashboardStatisticsUseCase` - Dashboard data
- `GetInspectionsUseCase` - Fetch inspections
- `GetHazardsUseCase` - Fetch hazards
- `GetUsersUseCase` - Fetch users

### 3. Data Layer
**Location:** `org.example.project.data`

- **DTOs**: Data Transfer Objects for API serialization
- **Services**: Ktor HTTP client implementations
- **Repository Implementations**: Concrete data access

**DTOs:**
- `AuthDto` - Login/Register request/response
- `UserDto` - User data
- `InspectionDto` - Inspection data
- `HazardDto` - Hazard data
- `DashboardDto` - Dashboard statistics
- `CoreDto` - Mines, Sites, Areas, Shafts
- `TemplateDto` - Inspection templates

**Services:**
- `AuthService` - Authentication API
- `UserService` - User management API
- `InspectionService` - Inspections API
- `HazardService` - Hazards API
- `DashboardService` - Dashboard statistics API

**HttpClient:**
- `SafeOpsHttpClient` - Configured Ktor client with:
  - JSON serialization (kotlinx.serialization)
  - Authentication interceptor
  - Error handling
  - Logging

## SOLID Principles Applied

### Single Responsibility Principle (SRP)
- Each class has one reason to change
- `LoginUseCase` only handles login logic
- `AuthService` only handles HTTP calls
- `AuthRepository` only coordinates data sources

### Open/Closed Principle (OCP)
- Use interfaces for repositories and services
- New implementations can be added without changing existing code
- `AuthRepository` interface allows different implementations

### Liskov Substitution Principle (LSP)
- Repository implementations can substitute interfaces
- Service implementations can be swapped

### Interface Segregation Principle (ISP)
- Small, focused interfaces
- `TokenProvider` only provides token-related methods
- Each service interface has only related methods

### Dependency Inversion Principle (DIP)
- Domain layer depends on abstractions (interfaces)
- Data layer provides concrete implementations
- ServiceLocator provides dependency injection

## Dependency Injection (Service Locator Pattern)

**Location:** `org.example.project.di.ServiceLocator`

```kotlin
// Provides all dependencies in one place
object ServiceLocator {
    val authService: AuthService by lazy { AuthServiceImpl(httpClient) }
    val authRepository: AuthRepository by lazy { AuthRepositoryImpl(authService) }
    fun provideLoginViewModel(): LoginViewModel = LoginViewModel(provideLoginUseCase())
}
```

**Benefits:**
- Centralized dependency management
- Easy to test (can swap implementations)
- Lazy initialization
- No external DI library needed for this project size

## API Integration

### Base URL
```
http://localhost:8080
```

### Authentication
- JWT Bearer tokens
- Automatic token refresh on 401
- Token storage in memory (extend to secure storage for production)

### Error Handling
```kotlin
sealed class ApiException(message: String, val statusCode: Int?) : Exception(message) {
    class NetworkError(message: String) : ApiException(message)
    class Unauthorized(message: String = "Unauthorized") : ApiException(message, 401)
    class Forbidden(message: String = "Forbidden") : ApiException(message, 403)
    class NotFound(message: String = "Not Found") : ApiException(message, 404)
    class BadRequest(message: String) : ApiException(message, 400)
    class ServerError(message: String = "Server Error") : ApiException(message, 500)
}
```

### Result Pattern
```kotlin
sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val message: String, val code: Int? = null) : Result<Nothing>()
    data object Loading : Result<Nothing>()
}
```

## Usage Example

### In a Composable Screen:
```kotlin
@Composable
fun LoginScreen(
    viewModel: LoginViewModel = remember { ServiceLocator.provideLoginViewModel() },
    onLoginSuccess: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    
    // Use uiState.isLoading, uiState.error, uiState.isSuccess
    // Call viewModel.login() when user clicks login button
}
```

## API Endpoints Mapped

From Bruno collection:

| Feature | Endpoint | Method | Auth |
|---------|----------|--------|------|
| Login | /api/v1/auth/login | POST | No |
| Register | /api/v1/auth/register | POST | No |
| Refresh Token | /api/v1/auth/refresh | POST | No |
| List Users | /api/auth/users | GET | Bearer |
| List Inspections | /api/v1/inspections | GET | Bearer |
| Create Inspection | /api/v1/inspections | POST | Bearer |
| List Hazards | /api/hazards | GET | Bearer |
| Create Hazard | /api/hazards | POST | Bearer |
| Dashboard Stats | /api/v1/dashboard/stats | GET | Bearer |
| List Mines | /api/core/mines | GET | Bearer |
| List Sites | /api/core/sites | GET | Bearer |
| List Areas | /api/core/areas | GET | Bearer |
| List Shafts | /api/core/shafts | GET | Bearer |

## Testing

Each layer can be tested independently:

1. **Domain Layer**: Test use cases with mock repositories
2. **Data Layer**: Test services with mock HTTP client
3. **Presentation Layer**: Test ViewModels with fake use cases

## Future Enhancements

1. **Secure Token Storage**: Use platform-specific secure storage
2. **Offline Support**: Add local database (Room/SQLDelight)
3. **Pagination**: Implement for large lists
4. **Caching**: Add memory/disk cache layer
5. **DI Framework**: Consider Koin for larger project
6. **Unit Tests**: Add comprehensive test coverage
