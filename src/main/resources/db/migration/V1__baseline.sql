-- ============================================================
-- V1: BASELINE MIGRATION
-- Purpose:
--   - Establish Flyway versioning
--   - Verify DB connectivity
--   - Create a neutral table not tied to any module
--   - Provide a safe anchor for future migrations
-- ============================================================

CREATE TABLE flyway_baseline (
    id BIGSERIAL PRIMARY KEY,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);