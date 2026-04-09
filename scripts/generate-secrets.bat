@echo off
REM SafeOps Secret Generator for Windows
REM Generates secure random secrets for production or development

setlocal enabledelayedexpansion

if "%~1"=="" (
    set ENVIRONMENT=dev
) else (
    set ENVIRONMENT=%~1
)

echo ===============================================
echo SafeOps Secret Generator
echo Environment: %ENVIRONMENT%
echo ===============================================
echo.

if /I "%ENVIRONMENT%"=="prod" goto :production
if /I "%ENVIRONMENT%"=="production" goto :production
goto :development

:production
echo Generating production secrets...
echo.

REM Generate random secrets using PowerShell for better randomness
for /f "usebackq delims=" %%a in (`powershell -Command "[Convert]::ToBase64String([byte[]](1..48 | ForEach-Object { Get-Random -Maximum 256 }))"`) do set JWT_SECRET_1=%%a
for /f "usebackq delims=" %%a in (`powershell -Command "[Convert]::ToBase64String([byte[]](1..48 | ForEach-Object { Get-Random -Maximum 256 }))"`) do set JWT_SECRET_2=%%a
for /f "usebackq delims=" %%a in (`powershell -Command "[Convert]::ToBase64String([byte[]](1..24 | ForEach-Object { Get-Random -Maximum 256 }))"`) do set DB_PASS=%%a
for /f "usebackq delims=" %%a in (`powershell -Command "[Convert]::ToBase64String([byte[]](1..24 | ForEach-Object { Get-Random -Maximum 256 }))"`) do set PG_PASS=%%a
for /f "usebackq delims=" %%a in (`powershell -Command "[Convert]::ToBase64String([byte[]](1..24 | ForEach-Object { Get-Random -Maximum 256 }))"`) do set PGADMIN_PASS=%%a
for /f "usebackq delims=" %%a in (`powershell -Command "[Convert]::ToBase64String([byte[]](1..24 | ForEach-Object { Get-Random -Maximum 256 }))"`) do set GRAFANA_PASS=%%a

(
echo # SafeOps Production Environment Configuration
echo # Generated: %date% %time%
echo # WARNING: This is a PRODUCTION environment configuration
echo.
echo # =============================================================================
echo # REQUIRED VARIABLES - MUST BE SET FOR PRODUCTION
echo # =============================================================================
echo.
echo # Frontend API Configuration
echo SAFEOPS_API_URL=https://your-production-domain.com
echo.
echo # Database Configuration
echo SPRING_DATASOURCE_URL=jdbc:postgresql://your-db-host:5432/safeops
echo SPRING_DATASOURCE_USERNAME=safeops
echo SPRING_DATASOURCE_PASSWORD=%DB_PASS%
echo.
echo # PostgreSQL Database (for Docker Compose)
echo POSTGRES_PASSWORD=%PG_PASS%
echo.
echo # JWT Secrets - GENERATED SECURE VALUES
echo # IMPORTANT: Keep these secure and back them up safely
echo APP_JWT_ACCESS_SECRET=%JWT_SECRET_1%
echo APP_JWT_REFRESH_SECRET=%JWT_SECRET_2%
echo.
echo # Monitoring Tools (Docker Compose)
echo PGADMIN_DEFAULT_PASSWORD=%PGADMIN_PASS%
echo GF_SECURITY_ADMIN_PASSWORD=%GRAFANA_PASS%
echo.
echo # =============================================================================
echo # OPTIONAL VARIABLES
echo # =============================================================================
echo.
echo # PgAdmin Configuration
echo PGADMIN_DEFAULT_EMAIL=admin@yourdomain.com
echo.
echo # Grafana Configuration
echo GF_SECURITY_ADMIN_USER=admin
echo.
echo # =============================================================================
echo # EXTERNAL SERVICES (Configure as needed)
echo # =============================================================================
echo.
echo # AWS S3 for file storage
echo # AWS_S3_BUCKET=your-bucket
echo # AWS_ACCESS_KEY_ID=your-key
echo # AWS_SECRET_ACCESS_KEY=your-secret
echo.
echo # Email Services
echo # SENDGRID_API_KEY=your-key
echo # TWILIO_ACCOUNT_SID=your-sid
echo # TWILIO_AUTH_TOKEN=your-token
echo.
echo # AI Providers
echo # OPENAI_API_KEY=your-key
echo # AWS_REKOGNITION_ACCESS_KEY=your-key
echo # AWS_REKOGNITION_SECRET_KEY=your-secret
echo.
echo # Redis (for distributed caching)
echo # SPRING_REDIS_HOST=localhost
echo # SPRING_REDIS_PORT=6379
echo # SPRING_REDIS_PASSWORD=
) > .env

echo.
echo ===============================================
echo ✅ Production .env file created with secure secrets!
echo ===============================================
echo.
echo ⚠️  IMPORTANT SECURITY WARNINGS:
echo    1. Keep the JWT secrets backed up safely - they cannot be recovered if lost
echo    2. Store this .env file securely (use a password manager or secrets vault)
echo    3. Never commit .env to version control
echo    4. Rotate secrets regularly (recommended: every 90 days)
echo.
echo 📝 Next steps:
echo    1. Update SAFEOPS_API_URL with your production domain
echo    2. Update SPRING_DATASOURCE_URL with your database host
echo    3. Update PGADMIN_DEFAULT_EMAIL with your admin email
echo    4. Configure external services (AWS, SendGrid, etc.) as needed
goto :end

:development
echo Generating development secrets...
echo.

REM Generate random values for development
set RAND1=!random!!random!
set RAND2=!random!!random!
set RAND3=!random!!random!
set RAND4=!random!!random!
set RAND5=!random!!random!

(
echo # SafeOps Development Environment Configuration
echo # Generated: %date% %time%
echo.
echo # =============================================================================
echo # REQUIRED VARIABLES
echo # =============================================================================
echo.
echo # Frontend API Configuration
echo SAFEOPS_API_URL=http://localhost:8080
echo.
echo # Database Configuration
echo SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/safeops
echo SPRING_DATASOURCE_USERNAME=safeops
echo SPRING_DATASOURCE_PASSWORD=dev_password_!RAND1!
echo.
echo # PostgreSQL Database (for Docker Compose)
echo POSTGRES_PASSWORD=postgres_!RAND2!
echo.
echo # JWT Secrets - Development only (use 'prod' argument for production)
echo APP_JWT_ACCESS_SECRET=dev-access-!RAND3!-!RAND4!
echo APP_JWT_REFRESH_SECRET=dev-refresh-!RAND3!-!RAND5!
echo.
echo # Monitoring Tools (Docker Compose)
echo PGADMIN_DEFAULT_PASSWORD=pgadmin_!RAND1!
echo GF_SECURITY_ADMIN_PASSWORD=grafana_!RAND2!
echo.
echo # =============================================================================
echo # OPTIONAL VARIABLES (with defaults)
echo # =============================================================================
echo.
echo # PgAdmin Configuration
echo PGADMIN_DEFAULT_EMAIL=admin@safeops.com
echo.
echo # Grafana Configuration
echo GF_SECURITY_ADMIN_USER=admin
echo.
echo # =============================================================================
echo # EXTERNAL SERVICES (Uncomment and configure as needed)
echo # =============================================================================
echo.
echo # AWS S3 for file storage
echo # AWS_S3_BUCKET=your-bucket
echo # AWS_ACCESS_KEY_ID=your-key
echo # AWS_SECRET_ACCESS_KEY=your-secret
echo.
echo # Email Services
echo # SENDGRID_API_KEY=your-key
echo # TWILIO_ACCOUNT_SID=your-sid
echo # TWILIO_AUTH_TOKEN=your-token
echo.
echo # AI Providers
echo # OPENAI_API_KEY=your-key
echo # AWS_REKOGNITION_ACCESS_KEY=your-key
echo # AWS_REKOGNITION_SECRET_KEY=your-secret
echo.
echo # Redis (for distributed caching)
echo # SPRING_REDIS_HOST=localhost
echo # SPRING_REDIS_PORT=6379
echo # SPRING_REDIS_PASSWORD=
) > .env

echo.
echo ===============================================
echo ✅ Development .env file created!
echo ===============================================
echo.
echo 📝 Next steps:
echo    1. Run: docker-compose up -d
echo    2. Access the application at http://localhost:8080
echo.
echo 💡 For production deployment, run: .\scripts\generate-secrets.bat prod

goto :end

:end
endlocal
