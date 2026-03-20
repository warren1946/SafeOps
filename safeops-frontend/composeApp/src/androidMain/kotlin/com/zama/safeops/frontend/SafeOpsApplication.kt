package com.zama.safeops.frontend

import android.app.Application
import com.zama.safeops.frontend.di.initKoin
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier

class SafeOpsApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        Napier.base(DebugAntilog())

        initKoin {
            androidContext(this@SafeOpsApplication)
        }
    }
}
