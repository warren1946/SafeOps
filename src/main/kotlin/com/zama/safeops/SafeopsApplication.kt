/*
 * Copyright (c) 2026 WH Mtawu.
 * Open Source under the MIT License.
 * Permission granted for use, modification, and distribution with attribution.
 * No warranty provided.
 */

package com.zama.safeops

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.persistence.autoconfigure.EntityScan
import org.springframework.boot.runApplication
import org.springframework.cache.annotation.EnableCaching
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EntityScan("com.zama.safeops")
@EnableJpaRepositories("com.zama.safeops")
@EnableCaching
@EnableAsync
@EnableScheduling
class SafeopsApplication

fun main(args: Array<String>) {
    runApplication<SafeopsApplication>(*args)
}
