ALTER TABLE hazards
    ADD COLUMN severity VARCHAR(20) NOT NULL DEFAULT 'MEDIUM',
    ADD COLUMN priority VARCHAR(10) NOT NULL DEFAULT 'P3',
    ADD COLUMN due_date TIMESTAMP NULL,
    ADD COLUMN resolved_at TIMESTAMP NULL,
    ADD COLUMN created_by BIGINT NULL;

ALTER TABLE hazards
    ADD CONSTRAINT fk_hazard_created_by
        FOREIGN KEY (created_by)
        REFERENCES app_user(id);

CREATE INDEX idx_hazards_severity ON hazards(severity);
CREATE INDEX idx_hazards_priority ON hazards(priority);
CREATE INDEX idx_hazards_due_date ON hazards(due_date);
CREATE INDEX idx_hazards_created_by ON hazards(created_by);