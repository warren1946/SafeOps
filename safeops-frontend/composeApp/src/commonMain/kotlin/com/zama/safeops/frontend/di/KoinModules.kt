package com.zama.safeops.frontend.di

import com.zama.safeops.frontend.data.api.SafeOpsApi
import com.zama.safeops.frontend.data.repository.AuthRepository
import com.zama.safeops.frontend.data.repository.InspectionRepository
import com.zama.safeops.frontend.data.repository.HazardRepository
import com.zama.safeops.frontend.data.repository.SafetyScoreRepository
import com.zama.safeops.frontend.domain.usecase.auth.LoginUseCase
import com.zama.safeops.frontend.domain.usecase.auth.LogoutUseCase
import com.zama.safeops.frontend.domain.usecase.auth.GetCurrentUserUseCase
import com.zama.safeops.frontend.domain.usecase.inspection.GetInspectionsUseCase
import com.zama.safeops.frontend.domain.usecase.hazard.GetHazardsUseCase
import com.zama.safeops.frontend.domain.usecase.hazard.CreateHazardUseCase
import com.zama.safeops.frontend.domain.usecase.safety.GetSafetyScoreUseCase
import com.zama.safeops.frontend.presentation.screens.auth.AuthViewModel
import com.zama.safeops.frontend.presentation.screens.dashboard.DashboardViewModel
import com.zama.safeops.frontend.presentation.screens.inspections.InspectionsViewModel
import com.zama.safeops.frontend.presentation.screens.hazards.HazardsViewModel
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module

fun initKoin(appDeclaration: KoinAppDeclaration = {}) =
    startKoin {
        appDeclaration()
        modules(
            networkModule,
            repositoryModule,
            useCaseModule,
            viewModelModule
        )
    }

val networkModule = module {
    single {
        Json {
            ignoreUnknownKeys = true
            isLenient = true
            encodeDefaults = true
            prettyPrint = true
            coerceInputValues = true
        }
    }

    single {
        HttpClient {
            install(ContentNegotiation) {
                json(get())
            }
            install(Logging) {
                level = LogLevel.ALL
            }
            install(HttpTimeout) {
                requestTimeoutMillis = 30000
                connectTimeoutMillis = 10000
                socketTimeoutMillis = 30000
            }
            defaultRequest {
                contentType(ContentType.Application.Json)
            }
        }
    }

    single { SafeOpsApi(get()) }
}

val repositoryModule = module {
    single { AuthRepository(get()) }
    single { InspectionRepository(get()) }
    single { HazardRepository(get()) }
    single { SafetyScoreRepository(get()) }
}

val useCaseModule = module {
    // Auth
    factory { LoginUseCase(get()) }
    factory { LogoutUseCase(get()) }
    factory { GetCurrentUserUseCase(get()) }

    // Inspections
    factory { GetInspectionsUseCase(get()) }

    // Hazards
    factory { GetHazardsUseCase(get()) }
    factory { CreateHazardUseCase(get()) }

    // Safety Score
    factory { GetSafetyScoreUseCase(get()) }
}

val viewModelModule = module {
    factory { AuthViewModel(get(), get(), get()) }
    factory { DashboardViewModel(get(), get(), get()) }
    factory { InspectionsViewModel(get()) }
    factory { HazardsViewModel(get(), get()) }
}
