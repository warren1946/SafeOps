-- Audit logs table for compliance tracking
CREATE TABLE audit_logs (
    id UUID PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    user_id BIGINT,
    user_email VARCHAR(255),
    action VARCHAR(50) NOT NULL,
    entity_type VARCHAR(100) NOT NULL,
    entity_id VARCHAR(100),
    description VARCHAR(1000) NOT NULL,
    old_values JSONB,
    new_values JSONB,
    ip_address VARCHAR(45),
    user_agent VARCHAR(500),
    session_id VARCHAR(100),
    success BOOLEAN NOT NULL DEFAULT TRUE,
    error_message VARCHAR(2000),
    metadata JSONB DEFAULT '{}',
    timestamp TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Indexes for common query patterns
CREATE INDEX idx_audit_tenant ON audit_logs(tenant_id);
CREATE INDEX idx_audit_user ON audit_logs(user_id);
CREATE INDEX idx_audit_entity ON audit_logs(entity_type, entity_id);
CREATE INDEX idx_audit_action ON audit_logs(action);
CREATE INDEX idx_audit_timestamp ON audit_logs(timestamp);
CREATE INDEX idx_audit_tenant_time ON audit_logs(tenant_id, timestamp);

-- Partial index for failed operations (for security monitoring)
CREATE INDEX idx_audit_failures ON audit_logs(tenant_id, timestamp) 
    WHERE success = FALSE;

-- Index for text search on description
CREATE INDEX idx_audit_description ON audit_logs USING gin(to_tsvector('english', description));

-- Table partitioning by month for efficient data retention
-- Note: This is PostgreSQL specific. For other databases, adjust accordingly.
-- Partitioning setup would typically be done in a separate migration for production.

-- Grant permissions (adjust based on your security model)
-- GRANT SELECT, INSERT ON audit_logs TO safeops_app;

-- Add table comment
COMMENT ON TABLE audit_logs IS 'Audit trail for all significant operations in the system for compliance and security';
