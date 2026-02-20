CREATE TABLE inspections (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    target_type VARCHAR(50) NOT NULL,
    target_id BIGINT NOT NULL,
    inspector_id BIGINT NOT NULL,
    assigned_reviewer_id BIGINT NULL,
    reviewer_comments TEXT NULL,   -- NEW
    status VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT chk_inspection_status CHECK (
        status IN ('DRAFT', 'SUBMITTED', 'APPROVED', 'REJECTED')
    ),
    CONSTRAINT chk_inspection_target_type CHECK (
        target_type IN ('AREA', 'SHAFT', 'SITE', 'EQUIPMENT')
    ),
    CONSTRAINT fk_inspection_inspector
        FOREIGN KEY (inspector_id) REFERENCES app_user(id),
    CONSTRAINT fk_inspection_reviewer
        FOREIGN KEY (assigned_reviewer_id) REFERENCES app_user(id)
);

CREATE INDEX idx_inspections_target ON inspections(target_type, target_id);
CREATE INDEX idx_inspections_inspector ON inspections(inspector_id);
CREATE INDEX idx_inspections_reviewer ON inspections(assigned_reviewer_id);
CREATE INDEX idx_inspections_status ON inspections(status);

CREATE OR REPLACE FUNCTION update_inspections_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = NOW();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_update_inspections_updated_at
BEFORE UPDATE ON inspections
FOR EACH ROW
EXECUTE FUNCTION update_inspections_updated_at();