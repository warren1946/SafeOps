#!/bin/bash

# SafeOps Secret Generator
# Generates secure random secrets for production or development

set -e

ENVIRONMENT=${1:-dev}

echo "==============================================="
echo "SafeOps Secret Generator"
echo "Environment: $ENVIRONMENT"
echo "==============================================="
echo ""

# Function to generate a random secret
generate_secret() {
    openssl rand -base64 48 | tr -d '\n' | tr -d '\r'
}

# Function to generate a random password
generate_password() {
    openssl rand -base64 24 | tr -d '\n' | tr -d '\r'
}

echo "Generating secrets for $ENVIRONMENT environment..."
echo ""

if [ "$ENVIRONMENT" = "prod" ] || [ "$ENVIRONMENT" = "production" ]; then
    cat > .env << EOF
# SafeOps Production Environment Configuration
# Generated: $(date)
# WARNING: This is a PRODUCTION environment configuration

# =============================================================================
# REQUIRED VARIABLES - MUST BE SET FOR PRODUCTION
# =============================================================================

# Frontend API Configuration
SAFEOPS_API_URL=https://your-production-domain.com

# Database Configuration
SPRING_DATASOURCE_URL=jdbc:postgresql://your-db-host:5432/safeops
SPRING_DATASOURCE_USERNAME=safeops
SPRING_DATASOURCE_PASSWORD=$(generate_password)

# PostgreSQL Database (for Docker Compose)
POSTGRES_PASSWORD=$(generate_password)

# JWT Secrets - GENERATED SECURE VALUES
# IMPORTANT: Keep these secure and back them up safely
APP_JWT_ACCESS_SECRET=$(generate_secret)
APP_JWT_REFRESH_SECRET=$(generate_secret)

# Monitoring Tools (Docker Compose)
PGADMIN_DEFAULT_PASSWORD=$(generate_password)
GF_SECURITY_ADMIN_PASSWORD=$(generate_password)

# =============================================================================
# OPTIONAL VARIABLES
# =============================================================================

# PgAdmin Configuration
PGADMIN_DEFAULT_EMAIL=admin@yourdomain.com

# Grafana Configuration
GF_SECURITY_ADMIN_USER=admin

# =============================================================================
# EXTERNAL SERVICES (Configure as needed)
# =============================================================================

# AWS S3 for file storage
# AWS_S3_BUCKET=your-bucket
# AWS_ACCESS_KEY_ID=your-key
# AWS_SECRET_ACCESS_KEY=your-secret

# Email Services
# SENDGRID_API_KEY=your-key
# TWILIO_ACCOUNT_SID=your-sid
# TWILIO_AUTH_TOKEN=your-token

# AI Providers
# OPENAI_API_KEY=your-key
# AWS_REKOGNITION_ACCESS_KEY=your-key
# AWS_REKOGNITION_SECRET_KEY=your-secret

# Redis (for distributed caching)
# SPRING_REDIS_HOST=localhost
# SPRING_REDIS_PORT=6379
# SPRING_REDIS_PASSWORD=
EOF
    echo "✅ Production .env file created with secure secrets!"
    echo ""
    echo "⚠️  IMPORTANT SECURITY WARNINGS:"
    echo "   1. Keep the JWT secrets backed up safely - they cannot be recovered if lost"
    echo "   2. Store this .env file securely (use a password manager or secrets vault)"
    echo "   3. Never commit .env to version control"
    echo "   4. Rotate secrets regularly (recommended: every 90 days)"
    echo ""
    echo "📝 Next steps:"
    echo "   1. Update SAFEOPS_API_URL with your production domain"
    echo "   2. Update SPRING_DATASOURCE_URL with your database host"
    echo "   3. Update PGADMIN_DEFAULT_EMAIL with your admin email"
    echo "   4. Configure external services (AWS, SendGrid, etc.) as needed"
    
else
    cat > .env << EOF
# SafeOps Development Environment Configuration
# Generated: $(date)

# =============================================================================
# REQUIRED VARIABLES
# =============================================================================

# Frontend API Configuration
SAFEOPS_API_URL=http://localhost:8080

# Database Configuration
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/safeops
SPRING_DATASOURCE_USERNAME=safeops
SPRING_DATASOURCE_PASSWORD=dev_password_$(openssl rand -hex 4)

# PostgreSQL Database (for Docker Compose)
POSTGRES_PASSWORD=postgres_$(openssl rand -hex 4)

# JWT Secrets - Development only (use generate-secrets.sh prod for production)
APP_JWT_ACCESS_SECRET=dev-access-$(openssl rand -hex 16)
APP_JWT_REFRESH_SECRET=dev-refresh-$(openssl rand -hex 16)

# Monitoring Tools (Docker Compose)
PGADMIN_DEFAULT_PASSWORD=pgadmin_$(openssl rand -hex 4)
GF_SECURITY_ADMIN_PASSWORD=grafana_$(openssl rand -hex 4)

# =============================================================================
# OPTIONAL VARIABLES (with defaults)
# =============================================================================

# PgAdmin Configuration
PGADMIN_DEFAULT_EMAIL=admin@safeops.com

# Grafana Configuration
GF_SECURITY_ADMIN_USER=admin

# =============================================================================
# EXTERNAL SERVICES (Uncomment and configure as needed)
# =============================================================================

# AWS S3 for file storage
# AWS_S3_BUCKET=your-bucket
# AWS_ACCESS_KEY_ID=your-key
# AWS_SECRET_ACCESS_KEY=your-secret

# Email Services
# SENDGRID_API_KEY=your-key
# TWILIO_ACCOUNT_SID=your-sid
# TWILIO_AUTH_TOKEN=your-token

# AI Providers
# OPENAI_API_KEY=your-key
# AWS_REKOGNITION_ACCESS_KEY=your-key
# AWS_REKOGNITION_SECRET_KEY=your-secret

# Redis (for distributed caching)
# SPRING_REDIS_HOST=localhost
# SPRING_REDIS_PORT=6379
# SPRING_REDIS_PASSWORD=
EOF
    echo "✅ Development .env file created!"
    echo ""
    echo "📝 Next steps:"
    echo "   1. Run: docker-compose up -d"
    echo "   2. Access the application at http://localhost:8080"
    echo ""
    echo "💡 For production deployment, run: ./scripts/generate-secrets.sh prod"
fi

echo ""
echo "==============================================="
echo "Secret generation complete!"
echo "==============================================="
