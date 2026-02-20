CREATE TABLE safety_reports (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    period_start TIMESTAMP NOT NULL,
    period_end TIMESTAMP NOT NULL,
    created_by BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    summary TEXT NOT NULL,
    event_count INT NOT NULL,
    high_severity_count INT NOT NULL,
    critical_severity_count INT NOT NULL,
    CONSTRAINT fk_safety_report_created_by
        FOREIGN KEY (created_by) REFERENCES app_user(id)
);

CREATE INDEX idx_safety_reports_period ON safety_reports(period_start, period_end);
CREATE INDEX idx_safety_reports_created_by ON safety_reports(created_by);