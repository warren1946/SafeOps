CREATE TABLE inspection_items (
    id BIGSERIAL PRIMARY KEY,
    inspection_id BIGINT NOT NULL REFERENCES inspections(id) ON DELETE CASCADE,
    title VARCHAR(255) NOT NULL,
    status VARCHAR(50) NOT NULL,
    comment TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    -- ENUM-like constraint
    CONSTRAINT chk_inspection_item_status CHECK (
        status IN ('PASS', 'FAIL', 'NOT_APPLICABLE')
    )
);

-- Index for fast lookup of items by inspection
CREATE INDEX idx_inspection_items_inspection_id ON inspection_items(inspection_id);