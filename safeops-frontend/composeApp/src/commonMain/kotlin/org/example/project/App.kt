package org.example.project

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import org.example.project.ui.screens.auth.LoginScreen
import org.example.project.ui.screens.auth.RegisterScreen
import org.example.project.ui.screens.dashboard.DashboardScreen
import org.example.project.ui.theme.SafeOpsTheme

enum class Screen {
    LOGIN,
    REGISTER,
    DASHBOARD
}

@Composable
fun App() {
    SafeOpsTheme {
        var currentScreen by remember { mutableStateOf(Screen.LOGIN) }
        
        when (currentScreen) {
            Screen.LOGIN -> {
                LoginScreen(
                    onLoginSuccess = { currentScreen = Screen.DASHBOARD },
                    onNavigateToRegister = { currentScreen = Screen.REGISTER }
                )
            }
            Screen.REGISTER -> {
                RegisterScreen(
                    onRegisterSuccess = { currentScreen = Screen.LOGIN },
                    onNavigateToLogin = { currentScreen = Screen.LOGIN }
                )
            }
            Screen.DASHBOARD -> {
                DashboardScreen(
                    onLogout = { currentScreen = Screen.LOGIN }
                )
            }
        }
    }
}

