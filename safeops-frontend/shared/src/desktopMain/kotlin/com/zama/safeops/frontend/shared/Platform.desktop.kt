package com.zama.safeops.frontend.shared

class DesktopPlatform : Platform {
    override val name: String = "Desktop"
}

actual fun getPlatform(): Platform = DesktopPlatform()
