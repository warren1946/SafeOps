package com.zama.safeops

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class SafeopsApplication

fun main(args: Array<String>) {
	runApplication<SafeopsApplication>(*args)
}
