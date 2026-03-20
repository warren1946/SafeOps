package com.zama.safeops.frontend.app

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.SlideTransition
import com.zama.safeops.frontend.presentation.screens.splash.SplashScreen
import com.zama.safeops.frontend.presentation.theme.SafeOpsTheme
import org.koin.compose.KoinContext

@Composable
fun SafeOpsApp() {
    KoinContext {
        SafeOpsTheme {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                Navigator(SplashScreen()) { navigator ->
                    SlideTransition(navigator)
                }
            }
        }
    }
}
