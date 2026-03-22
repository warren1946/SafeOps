package org.example.project.di

import io.ktor.client.HttpClient
import org.example.project.data.remote.service.AuthService
import org.example.project.data.remote.service.AuthServiceImpl
import org.example.project.data.remote.service.DashboardService
import org.example.project.data.remote.service.DashboardServiceImpl
import org.example.project.data.remote.service.HazardService
import org.example.project.data.remote.service.HazardServiceImpl
import org.example.project.data.remote.service.InspectionService
import org.example.project.data.remote.service.InspectionServiceImpl
import org.example.project.data.remote.service.SafeOpsHttpClient
import org.example.project.data.remote.service.UserService
import org.example.project.data.remote.service.UserServiceImpl
import org.example.project.data.repository.AuthRepositoryImpl
import org.example.project.data.repository.DashboardRepositoryImpl
import org.example.project.data.repository.HazardRepositoryImpl
import org.example.project.data.repository.InspectionRepositoryImpl
import org.example.project.data.repository.UserRepositoryImpl
import org.example.project.domain.repository.AuthRepository
import org.example.project.domain.repository.DashboardRepository
import org.example.project.domain.repository.HazardRepository
import org.example.project.domain.repository.InspectionRepository
import org.example.project.domain.repository.UserRepository
import org.example.project.domain.usecase.GetCurrentUserUseCase
import org.example.project.domain.usecase.GetDashboardStatisticsUseCase
import org.example.project.domain.usecase.GetHazardsUseCase
import org.example.project.domain.usecase.GetInspectionsUseCase
import org.example.project.domain.usecase.GetUsersUseCase
import org.example.project.domain.usecase.LoginUseCase
import org.example.project.domain.usecase.LogoutUseCase
import org.example.project.domain.usecase.RegisterUseCase
import org.example.project.presentation.viewmodel.DashboardViewModel
import org.example.project.presentation.viewmodel.HazardsViewModel
import org.example.project.presentation.viewmodel.InspectionsViewModel
import org.example.project.presentation.viewmodel.LoginViewModel
import org.example.project.presentation.viewmodel.RegisterViewModel
import org.example.project.presentation.viewmodel.UsersViewModel

/**
 * Service Locator Pattern for Dependency Injection
 * Provides a centralized registry for all dependencies
 * 
 * Note: For larger projects, consider using Koin or Kodein
 */
object ServiceLocator {
    
    // HttpClient (Singleton)
    private val httpClient: HttpClient by lazy {
        SafeOpsHttpClient.createSimpleClient()
    }
    
    // Services (Singleton)
    val authService: AuthService by lazy {
        AuthServiceImpl(httpClient)
    }
    
    val userService: UserService by lazy {
        UserServiceImpl(httpClient)
    }
    
    val inspectionService: InspectionService by lazy {
        InspectionServiceImpl(httpClient)
    }
    
    val hazardService: HazardService by lazy {
        HazardServiceImpl(httpClient)
    }
    
    val dashboardService: DashboardService by lazy {
        DashboardServiceImpl(httpClient)
    }
    
    // Repositories (Singleton)
    val authRepository: AuthRepository by lazy {
        AuthRepositoryImpl(authService)
    }
    
    val userRepository: UserRepository by lazy {
        UserRepositoryImpl(userService)
    }
    
    val inspectionRepository: InspectionRepository by lazy {
        InspectionRepositoryImpl(inspectionService)
    }
    
    val hazardRepository: HazardRepository by lazy {
        HazardRepositoryImpl(hazardService)
    }
    
    val dashboardRepository: DashboardRepository by lazy {
        DashboardRepositoryImpl(dashboardService)
    }
    
    // Use Cases
    fun provideLoginUseCase(): LoginUseCase = LoginUseCase(authRepository)
    fun provideRegisterUseCase(): RegisterUseCase = RegisterUseCase(authRepository)
    fun provideLogoutUseCase(): LogoutUseCase = LogoutUseCase(authRepository)
    fun provideGetCurrentUserUseCase(): GetCurrentUserUseCase = GetCurrentUserUseCase(authRepository)
    fun provideGetDashboardStatisticsUseCase(): GetDashboardStatisticsUseCase = 
        GetDashboardStatisticsUseCase(dashboardRepository)
    fun provideGetInspectionsUseCase(): GetInspectionsUseCase = GetInspectionsUseCase(inspectionRepository)
    fun provideGetHazardsUseCase(): GetHazardsUseCase = GetHazardsUseCase(hazardRepository)
    fun provideGetUsersUseCase(): GetUsersUseCase = GetUsersUseCase(userRepository)
    
    // ViewModels (Factory methods - create new instances)
    fun provideLoginViewModel(): LoginViewModel = LoginViewModel(provideLoginUseCase())
    fun provideRegisterViewModel(): RegisterViewModel = RegisterViewModel(provideRegisterUseCase())
    fun provideDashboardViewModel(): DashboardViewModel = DashboardViewModel(
        provideGetDashboardStatisticsUseCase(),
        provideGetCurrentUserUseCase(),
        provideLogoutUseCase()
    )
    fun provideInspectionsViewModel(): InspectionsViewModel = 
        InspectionsViewModel(provideGetInspectionsUseCase())
    fun provideHazardsViewModel(): HazardsViewModel = 
        HazardsViewModel(provideGetHazardsUseCase())
    fun provideUsersViewModel(): UsersViewModel = 
        UsersViewModel(provideGetUsersUseCase())
}
