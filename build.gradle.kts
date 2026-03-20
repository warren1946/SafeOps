plugins {
    kotlin("jvm") version "2.2.21"
    kotlin("plugin.spring") version "2.2.21"
    id("org.springframework.boot") version "4.0.2"
    id("io.spring.dependency-management") version "1.1.7"
    kotlin("plugin.jpa") version "2.2.21"
    kotlin("plugin.allopen") version "2.2.21"
}

group = "com.zama"
version = "0.0.1-SNAPSHOT"
description = "SafeOps - Mining Safety Management Platform"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    // ==================== Spring Boot Starters ====================
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-flyway")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-websocket")
    implementation("org.springframework.boot:spring-boot-starter-cache")

    // ==================== Kotlin ====================
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

    // ==================== Database ====================
    runtimeOnly("org.postgresql:postgresql")
    implementation("org.flywaydb:flyway-database-postgresql")

    // ==================== Security - JWT ====================
    implementation("io.jsonwebtoken:jjwt-api:0.11.5")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.11.5")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.11.5")

    // ==================== Async & Concurrency ====================
    implementation("org.springframework.boot:spring-boot-starter-integration")

    // ==================== PDF Generation (for Reports) ====================
    implementation("com.itextpdf:itext7-core:8.0.3")
    implementation("org.xhtmlrenderer:flying-saucer-pdf-openpdf:9.1.22")

    // ==================== Excel Export ====================
    implementation("org.apache.poi:poi-ooxml:5.2.5")

    // ==================== Email ====================
    implementation("org.springframework.boot:spring-boot-starter-mail")

    // ==================== Caching ====================
    implementation("com.github.ben-manes.caffeine:caffeine")

    // ==================== Monitoring ====================
    implementation("io.micrometer:micrometer-registry-prometheus")

    // ==================== API Documentation ====================
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.6.0")



    // ==================== Development ====================
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    developmentOnly("org.springframework.boot:spring-boot-docker-compose")

    // ==================== Testing ====================
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testImplementation("io.mockk:mockk:1.13.9")
    testImplementation("com.ninja-squad:springmockk:4.0.2")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    // ==================== Test Containers ====================
    testImplementation("org.testcontainers:junit-jupiter:1.19.7")
    testImplementation("org.testcontainers:postgresql:1.19.7")
    testImplementation("org.springframework.boot:spring-boot-test-autoconfigure")
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll(
            "-Xjsr305=strict",
            "-Xannotation-default-target=param-property"
        )
    }
}

allOpen {
    annotation("jakarta.persistence.Entity")
    annotation("jakarta.persistence.MappedSuperclass")
    annotation("jakarta.persistence.Embeddable")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

// ==================== Spring Boot Configuration ====================
springBoot {
    buildInfo()
}
