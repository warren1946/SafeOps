CREATE TABLE hazards (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    status VARCHAR(50) NOT NULL,
    assigned_to BIGINT,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

-- Optional but recommended: enforce referential integrity
ALTER TABLE hazards
ADD CONSTRAINT fk_hazard_assigned_to
FOREIGN KEY (assigned_to)
REFERENCES users(id);

-- Optional but recommended: performance indexes
CREATE INDEX idx_hazards_status ON hazards(status);
CREATE INDEX idx_hazards_assigned_to ON hazards(assigned_to);