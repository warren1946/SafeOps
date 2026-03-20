package com.zama.safeops.frontend.shared

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform
