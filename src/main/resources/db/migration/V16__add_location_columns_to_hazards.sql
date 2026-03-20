-- Add location columns to hazards table
ALTER TABLE hazards
    ADD COLUMN location_type VARCHAR(50) NULL,
    ADD COLUMN location_id BIGINT NULL;

-- Add index for location-based queries
CREATE INDEX idx_hazards_location ON hazards(location_type, location_id);
