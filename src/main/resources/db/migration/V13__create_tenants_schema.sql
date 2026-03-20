-- ============================================================
-- V13: CREATE TENANTS SCHEMA
-- Purpose:
--   - Add multi-tenancy support for white-label capability
--   - Store tenant configurations, branding, and settings
--   - Support for subscription plans and feature flags
-- ============================================================

-- Main tenants table
CREATE TABLE tenants (
    id BIGSERIAL PRIMARY KEY,
    slug VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(200) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    subscription_plan VARCHAR(20) NOT NULL DEFAULT 'BASIC',
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    activated_at TIMESTAMP,
    
    -- Configuration
    default_language VARCHAR(10) NOT NULL DEFAULT 'en',
    timezone VARCHAR(50) NOT NULL DEFAULT 'UTC',
    date_format VARCHAR(20) NOT NULL DEFAULT 'yyyy-MM-dd',
    time_format VARCHAR(20) NOT NULL DEFAULT 'HH:mm',
    
    -- Feature flags
    feature_whatsapp BOOLEAN NOT NULL DEFAULT TRUE,
    feature_advanced_reporting BOOLEAN NOT NULL DEFAULT FALSE,
    feature_custom_branding BOOLEAN NOT NULL DEFAULT FALSE,
    feature_api_access BOOLEAN NOT NULL DEFAULT FALSE,
    feature_multi_mine BOOLEAN NOT NULL DEFAULT TRUE,
    feature_offline_mode BOOLEAN NOT NULL DEFAULT FALSE,
    feature_photo_evidence BOOLEAN NOT NULL DEFAULT TRUE,
    feature_gps_tracking BOOLEAN NOT NULL DEFAULT TRUE,
    
    -- WhatsApp Configuration
    whatsapp_phone_id VARCHAR(100),
    whatsapp_account_id VARCHAR(100),
    whatsapp_access_token VARCHAR(500),
    whatsapp_webhook_secret VARCHAR(200),
    whatsapp_welcome_msg VARCHAR(500) DEFAULT 'Welcome to SafeOps! Send START INSPECTION to begin.',
    
    -- Notification Settings
    email_enabled BOOLEAN NOT NULL DEFAULT TRUE,
    sms_enabled BOOLEAN NOT NULL DEFAULT FALSE,
    push_enabled BOOLEAN NOT NULL DEFAULT TRUE,
    whatsapp_enabled BOOLEAN NOT NULL DEFAULT TRUE,
    
    -- Branding
    primary_color VARCHAR(10) DEFAULT '#1e3a5f',
    secondary_color VARCHAR(10) DEFAULT '#e63946',
    logo_url VARCHAR(500),
    favicon_url VARCHAR(500),
    app_name VARCHAR(100) DEFAULT 'SafeOps',
    support_email VARCHAR(200),
    support_phone VARCHAR(50)
);

-- Tenant supported languages
CREATE TABLE tenant_languages (
    tenant_id BIGINT NOT NULL REFERENCES tenants(id) ON DELETE CASCADE,
    language_code VARCHAR(10) NOT NULL,
    PRIMARY KEY (tenant_id, language_code)
);

-- Create indexes
CREATE INDEX idx_tenants_status ON tenants(status);
CREATE INDEX idx_tenants_slug ON tenants(slug);
CREATE INDEX idx_tenants_plan ON tenants(subscription_plan);

-- Insert default system tenant (for single-tenant mode migration)
INSERT INTO tenants (id, slug, name, status, subscription_plan, activated_at, default_language)
VALUES (1, 'default', 'Default Tenant', 'ACTIVE', 'ENTERPRISE', NOW(), 'en');

-- ============================================================
-- Update existing tables to support multi-tenancy
-- ============================================================

-- Add tenant_id to users table (app_user is the actual table name)
ALTER TABLE app_user ADD COLUMN tenant_id BIGINT DEFAULT 1 REFERENCES tenants(id);
CREATE INDEX idx_users_tenant ON app_user(tenant_id);

-- Add tenant_id to mines table
ALTER TABLE mines ADD COLUMN tenant_id BIGINT DEFAULT 1 REFERENCES tenants(id);
CREATE INDEX idx_mines_tenant ON mines(tenant_id);

-- Add tenant_id to sites table (through mine relationship)
-- Sites inherit tenant from their mine, no direct column needed

-- Add tenant_id to inspections table
ALTER TABLE inspections ADD COLUMN tenant_id BIGINT DEFAULT 1 REFERENCES tenants(id);
CREATE INDEX idx_inspections_tenant ON inspections(tenant_id);

-- Add tenant_id to hazards table
ALTER TABLE hazards ADD COLUMN tenant_id BIGINT DEFAULT 1 REFERENCES tenants(id);
CREATE INDEX idx_hazards_tenant ON hazards(tenant_id);

-- Add tenant_id to safety_events table
ALTER TABLE safety_events ADD COLUMN tenant_id BIGINT DEFAULT 1 REFERENCES tenants(id);
CREATE INDEX idx_safety_events_tenant ON safety_events(tenant_id);

-- Add tenant_id to templates table
ALTER TABLE inspection_templates ADD COLUMN tenant_id BIGINT DEFAULT 1 REFERENCES tenants(id);
CREATE INDEX idx_templates_tenant ON inspection_templates(tenant_id);

-- ============================================================
-- Create audit log table
-- ============================================================

CREATE TABLE audit_logs (
    id BIGSERIAL PRIMARY KEY,
    tenant_id BIGINT NOT NULL REFERENCES tenants(id),
    user_id BIGINT REFERENCES app_user(id),
    action VARCHAR(50) NOT NULL,
    entity_type VARCHAR(100) NOT NULL,
    entity_id VARCHAR(100),
    old_value TEXT,
    new_value TEXT,
    metadata JSONB,
    ip_address VARCHAR(45),
    user_agent TEXT,
    timestamp TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_audit_logs_tenant ON audit_logs(tenant_id);
CREATE INDEX idx_audit_logs_user ON audit_logs(user_id);
CREATE INDEX idx_audit_logs_action ON audit_logs(action);
CREATE INDEX idx_audit_logs_entity ON audit_logs(entity_type, entity_id);
CREATE INDEX idx_audit_logs_timestamp ON audit_logs(timestamp);

-- ============================================================
-- Create notifications table
-- ============================================================

CREATE TABLE notifications (
    id BIGSERIAL PRIMARY KEY,
    tenant_id BIGINT NOT NULL REFERENCES tenants(id),
    user_id BIGINT,
    channel VARCHAR(20) NOT NULL,
    type VARCHAR(50) NOT NULL,
    subject VARCHAR(500),
    content TEXT NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    priority VARCHAR(20) NOT NULL DEFAULT 'NORMAL',
    scheduled_at TIMESTAMP,
    sent_at TIMESTAMP,
    delivered_at TIMESTAMP,
    error_message TEXT,
    retry_count INT DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    
    -- Recipient details
    recipient_email VARCHAR(200),
    recipient_phone VARCHAR(50),
    recipient_device_token VARCHAR(500),
    recipient_whatsapp_id VARCHAR(100)
);

CREATE INDEX idx_notifications_tenant ON notifications(tenant_id);
CREATE INDEX idx_notifications_user ON notifications(user_id);
CREATE INDEX idx_notifications_status ON notifications(status);
CREATE INDEX idx_notifications_scheduled ON notifications(scheduled_at) WHERE scheduled_at IS NOT NULL;

-- ============================================================
-- Create WhatsApp conversations table
-- ============================================================

CREATE TABLE whatsapp_conversations (
    id VARCHAR(100) PRIMARY KEY,
    tenant_id BIGINT NOT NULL REFERENCES tenants(id),
    phone_number VARCHAR(50) NOT NULL,
    user_id BIGINT REFERENCES app_user(id),
    officer_name VARCHAR(200),
    state VARCHAR(50) NOT NULL DEFAULT 'IDLE',
    context JSONB DEFAULT '{}',
    started_at TIMESTAMP NOT NULL DEFAULT NOW(),
    last_activity_at TIMESTAMP NOT NULL DEFAULT NOW(),
    expires_at TIMESTAMP NOT NULL DEFAULT NOW() + INTERVAL '30 minutes',
    message_count INT DEFAULT 0
);

CREATE INDEX idx_whatsapp_conv_tenant ON whatsapp_conversations(tenant_id);
CREATE INDEX idx_whatsapp_conv_phone ON whatsapp_conversations(phone_number);
CREATE INDEX idx_whatsapp_conv_user ON whatsapp_conversations(user_id);
CREATE INDEX idx_whatsapp_conv_expires ON whatsapp_conversations(expires_at);

-- ============================================================
-- Create file storage metadata table
-- ============================================================

CREATE TABLE stored_files (
    file_key VARCHAR(500) PRIMARY KEY,
    tenant_id BIGINT NOT NULL REFERENCES tenants(id),
    original_filename VARCHAR(500) NOT NULL,
    content_type VARCHAR(100) NOT NULL,
    size_bytes BIGINT NOT NULL,
    category VARCHAR(50) NOT NULL,
    entity_id BIGINT,
    entity_type VARCHAR(100),
    uploaded_by BIGINT REFERENCES app_user(id),
    metadata JSONB,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_stored_files_tenant ON stored_files(tenant_id);
CREATE INDEX idx_stored_files_category ON stored_files(category);
CREATE INDEX idx_stored_files_entity ON stored_files(entity_type, entity_id);
