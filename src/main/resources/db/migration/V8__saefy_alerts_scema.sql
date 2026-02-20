CREATE TABLE safety_alerts (
    id BIGSERIAL PRIMARY KEY,
    event_id BIGINT NOT NULL REFERENCES safety_events(id) ON DELETE CASCADE,
    alert_type VARCHAR(50) NOT NULL,
    message TEXT NOT NULL,
    recipient_id BIGINT NOT NULL,
    acknowledged BOOLEAN NOT NULL DEFAULT FALSE,
    acknowledged_at TIMESTAMP NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT chk_safety_alert_type CHECK (
        alert_type IN ('HIGH_SEVERITY_EVENT', 'CRITICAL_SEVERITY_EVENT', 'MANUAL_ESCALATION', 'FOLLOW_UP_REQUIRED')
    ),
    CONSTRAINT fk_safety_alert_recipient
        FOREIGN KEY (recipient_id) REFERENCES app_user(id)
);

CREATE INDEX idx_safety_alerts_event_id ON safety_alerts(event_id);
CREATE INDEX idx_safety_alerts_recipient ON safety_alerts(recipient_id);
CREATE INDEX idx_safety_alerts_acknowledged ON safety_alerts(acknowledged);