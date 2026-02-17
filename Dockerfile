# ============================
# 1. BUILD STAGE
# ============================
FROM gradle:8.7-jdk21 AS build
WORKDIR /app

# Copy Gradle files first for caching
COPY build.gradle.kts settings.gradle.kts gradle.properties ./
COPY gradle ./gradle

# Download dependencies (cached)
RUN gradle dependencies --no-daemon || true

# Copy the rest of the project
COPY . .

# Build the application
RUN gradle clean bootJar --no-daemon

# ============================
# 2. RUNTIME STAGE
# ============================
FROM eclipse-temurin:21-jre

WORKDIR /app

# Copy the built JAR from the build stage
COPY --from=build /app/build/libs/*.jar app.jar

# Expose port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]