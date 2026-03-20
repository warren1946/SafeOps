@echo off
REM SafeOps Development Start Script for Windows
REM Usage: .\scripts\dev-start.bat [docker|local|test]

setlocal enabledelayedexpansion

if "%~1"=="" goto :docker
if /I "%~1"=="docker" goto :docker
if /I "%~1"=="local" goto :local
if /I "%~1"=="test" goto :test
if /I "%~1"=="setup" goto :setup
goto :usage

:docker
echo [INFO] Starting SafeOps with Docker Compose...

if not exist .env (
    echo [WARN] .env file not found, creating from template...
    (
        echo APP_JWT_ACCESS_SECRET=your-access-secret-at-least-256-bits-long-for-security
        echo APP_JWT_REFRESH_SECRET=your-refresh-secret-at-least-256-bits-long-for-security
    ) > .env
)

docker-compose up -d --build

echo [INFO] Services started!
echo [INFO] Application: http://localhost:8080
echo [INFO] Swagger UI: http://localhost:8080/swagger-ui.html
echo [INFO] PgAdmin: http://localhost:5050 (admin@safeops.com / admin)
echo [INFO] Grafana: http://localhost:3000 (admin / admin)
echo [INFO] MailHog: http://localhost:8025
goto :end

:local
echo [INFO] Starting SafeOps locally...
if "%SPRING_DATASOURCE_URL%"=="" (
    echo [ERROR] SPRING_DATASOURCE_URL environment variable not set
    echo [ERROR] Example: set SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/safeops
    exit /b 1
)
gradlew.bat bootRun
goto :end

:test
echo [INFO] Running tests...
gradlew.bat clean test
echo [INFO] Tests completed!
goto :end

:setup
echo [INFO] Setting up development environment...
if not exist .env (
    (
        echo APP_JWT_ACCESS_SECRET=dev-access-secret-at-least-256-bits-long-for-security-only
        echo APP_JWT_REFRESH_SECRET=dev-refresh-secret-at-least-256-bits-long-for-security-only
    ) > .env
)
echo [INFO] Development environment setup complete!
goto :end

:usage
echo Usage: %0 [docker^|local^|test^|setup]
echo.
echo Commands:
echo   docker  - Start with Docker Compose (default)
echo   local   - Start locally (requires PostgreSQL)
echo   test    - Run all tests
echo   setup   - Setup development environment
exit /b 1

:end
endlocal
