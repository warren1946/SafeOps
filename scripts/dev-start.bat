@echo off
REM SafeOps Development Start Script for Windows
REM Usage: .\scripts\dev-start.bat [docker|local|test|setup]

setlocal enabledelayedexpansion

if "%~1"=="" goto :docker
if /I "%~1"=="docker" goto :docker
if /I "%~1"=="local" goto :local
if /I "%~1"=="test" goto :test
if /I "%~1"=="setup" goto :setup
goto :usage

:docker
echo [INFO] Starting SafeOps with Docker Compose...

call :checkEnv
if !errorlevel! neq 0 exit /b 1

docker-compose up -d --build

echo [INFO] Services started!
echo [INFO] Application: http://localhost:8080
echo [INFO] Swagger UI: http://localhost:8080/swagger-ui.html
echo [INFO] PgAdmin: http://localhost:5050
echo [INFO] Grafana: http://localhost:3000
echo [INFO] MailHog: http://localhost:8025
goto :end

:local
echo [INFO] Starting SafeOps locally...
call :checkEnv
if !errorlevel! neq 0 exit /b 1

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
    echo [INFO] Creating .env file with secure secrets...
    call .\scripts\generate-secrets.bat dev
    echo [INFO] Development environment setup complete!
    echo [WARN] Review .env file and customize values as needed.
    echo [WARN] For production, use: .\scripts\generate-secrets.bat prod
) else (
    echo [INFO] .env file already exists. Remove it to regenerate.
)
goto :end

:checkEnv
if not exist .env (
    echo [ERROR] .env file not found!
    echo [ERROR] Please run: .\scripts\dev-start.bat setup
    exit /b 1
)
echo [INFO] Loading environment from .env file...
for /f "usebackq tokens=*" %%a in (.env) do (
    set line=%%a
    if not "!line:~0,1!"=="#" (
        if not "!line!"=="" (
            for /f "tokens=1,2 delims==" %%b in ("!line!") do (
                set "%%b=%%c"
            )
        )
    )
)
exit /b 0

:usage
echo Usage: %0 [docker^|local^|test^|setup]
echo.
echo Commands:
echo   docker  - Start with Docker Compose (default)
echo   local   - Start locally (requires PostgreSQL)
echo   test    - Run all tests
echo   setup   - Setup development environment with secure secrets
exit /b 1

:end
endlocal
