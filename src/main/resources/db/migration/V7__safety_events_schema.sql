CREATE TABLE safety_events (
    id BIGSERIAL PRIMARY KEY,
    type VARCHAR(50) NOT NULL,
    description TEXT NOT NULL,
    severity VARCHAR(50) NOT NULL,
    location_type VARCHAR(50) NOT NULL,
    location_id BIGINT NOT NULL,
    reporter_id BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT chk_safety_event_type CHECK (
        type IN ('INCIDENT', 'NEAR_MISS', 'UNSAFE_CONDITION', 'UNSAFE_ACT', 'OBSERVATION')
    ),
    CONSTRAINT chk_safety_event_severity CHECK (
        severity IN ('LOW', 'MEDIUM', 'HIGH', 'CRITICAL')
    ),
    CONSTRAINT chk_safety_event_location_type CHECK (
        location_type IN ('AREA', 'SHAFT', 'SITE', 'EQUIPMENT')
    ),
    CONSTRAINT fk_safety_event_reporter
        FOREIGN KEY (reporter_id) REFERENCES app_user(id)
);

CREATE INDEX idx_safety_events_severity ON safety_events(severity);
CREATE INDEX idx_safety_events_created_at ON safety_events(created_at);
CREATE INDEX idx_safety_events_location ON safety_events(location_type, location_id);