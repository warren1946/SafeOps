package com.zama.safeops.frontend.presentation.screens.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.zama.safeops.frontend.presentation.screens.auth.LoginScreen
import com.zama.safeops.frontend.presentation.screens.dashboard.DashboardScreen
import com.zama.safeops.frontend.presentation.theme.SafetyOrange
import kotlinx.coroutines.delay

class SplashScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val scale = remember { Animatable(0f) }

        LaunchedEffect(Unit) {
            scale.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 500)
            )
            delay(1500)
            // TODO: Check if user is authenticated
            navigator.replace(LoginScreen())
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Logo placeholder
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .scale(scale.value)
                        .background(SafetyOrange, MaterialTheme.shapes.extraLarge),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "SO",
                        style = MaterialTheme.typography.displayMedium,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "SafeOps",
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Text(
                    text = "Mining Safety Platform",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(48.dp))

                CircularProgressIndicator(
                    color = SafetyOrange,
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}
