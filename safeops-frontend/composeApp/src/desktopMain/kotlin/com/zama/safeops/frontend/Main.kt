package com.zama.safeops.frontend

import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.zama.safeops.frontend.app.SafeOpsApp
import com.zama.safeops.frontend.di.initKoin
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import java.awt.Dimension

fun main() {
    Napier.base(DebugAntilog())
    initKoin()

    application {
        Window(
            onCloseRequest = ::exitApplication,
            title = "SafeOps - Mining Safety Platform",
            state = rememberWindowState(width = 1440.dp, height = 900.dp)
        ) {
            window.minimumSize = Dimension(1024, 768)
            SafeOpsApp()
        }
    }
}
