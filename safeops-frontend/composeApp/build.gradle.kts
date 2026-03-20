import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.buildkonfig)
}

kotlin {
    androidTarget {
        compilations.all {
            kotlinOptions {
                jvmTarget = "17"
            }
        }
    }

    jvm("desktop")

    sourceSets {
        val desktopMain by getting

        commonMain.dependencies {
            // Compose
            implementation(libs.bundles.compose.ui)
            implementation(libs.compose.ui.tooling.preview)

            // AndroidX Lifecycle
            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.androidx.lifecycle.runtime)

            // Ktor Client
            implementation(libs.bundles.ktor.client)

            // KotlinX
            implementation(libs.bundles.kotlinx.common)

            // DI
            implementation(libs.bundles.koin.common)

            // Navigation
            implementation(libs.bundles.voyager)

            // Image Loading
            implementation(libs.coil.compose)
            implementation(libs.coil.network.ktor)

            // Logging
            implementation(libs.napier)

            // Local DB
            implementation(libs.realm.base)

            // Shared module
            implementation(project(":shared"))
        }

        androidMain.dependencies {
            implementation(libs.androidx.activity.compose)
            implementation(libs.androidx.core.ktx)
            implementation(libs.androidx.appcompat)
            implementation(libs.kotlinx.coroutines.android)
            implementation(libs.ktor.client.okhttp)
            implementation(libs.koin.android)
        }

        desktopMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutines.swing)
            implementation(libs.ktor.client.java)
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

android {
    namespace = "com.zama.safeops.frontend"
    compileSdk = 35

    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    sourceSets["main"].res.srcDirs("src/androidMain/res")
    sourceSets["main"].resources.srcDirs("src/commonMain/resources")

    defaultConfig {
        applicationId = "com.zama.safeops.frontend"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0.0"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        compose = true
    }
}

compose.desktop {
    application {
        mainClass = "com.zama.safeops.frontend.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "SafeOps"
            packageVersion = "1.0.0"

            windows {
                menu = true
                shortcut = true
                upgradeUuid = "a1b2c3d4-e5f6-7890-abcd-ef1234567890"
            }
        }
    }
}

buildkonfig {
    packageName = "com.zama.safeops.frontend"

    defaultConfigs {
        buildConfigField(com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING, "APP_NAME", "SafeOps")
        buildConfigField(com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING, "APP_VERSION", "1.0.0")
    }

    defaultConfigs("dev") {
        buildConfigField(com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING, "API_BASE_URL", "http://localhost:8080")
        buildConfigField(com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING, "API_VERSION", "v1")
        buildConfigField(com.codingfeline.buildkonfig.compiler.FieldSpec.Type.BOOLEAN, "DEBUG", "true")
    }

    defaultConfigs("staging") {
        buildConfigField(com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING, "API_BASE_URL", "https://staging.safeops.com")
        buildConfigField(com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING, "API_VERSION", "v1")
        buildConfigField(com.codingfeline.buildkonfig.compiler.FieldSpec.Type.BOOLEAN, "DEBUG", "true")
    }

    defaultConfigs("prod") {
        buildConfigField(com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING, "API_BASE_URL", "https://api.safeops.com")
        buildConfigField(com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING, "API_VERSION", "v1")
        buildConfigField(com.codingfeline.buildkonfig.compiler.FieldSpec.Type.BOOLEAN, "DEBUG", "false")
    }
}
