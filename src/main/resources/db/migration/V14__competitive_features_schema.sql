-- ============================================================
-- V14: COMPETITIVE FEATURES SCHEMA
-- Purpose:
--   - Offline sync for inspections
--   - AI image analysis storage
--   - IoT sensor data
--   - Gamification system
-- ============================================================

-- ============================================================
-- SYNC MODULE
-- ============================================================

CREATE TABLE sync_records (
    local_id VARCHAR(100) PRIMARY KEY,
    server_id BIGINT,
    tenant_id BIGINT NOT NULL REFERENCES tenants(id),
    user_id BIGINT NOT NULL REFERENCES app_user(id),
    entity_type VARCHAR(50) NOT NULL,
    operation VARCHAR(20) NOT NULL,  -- CREATE, UPDATE, DELETE
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    local_data JSONB NOT NULL,
    checksum VARCHAR(100) NOT NULL,
    retry_count INT DEFAULT 0,
    last_error TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    modified_at TIMESTAMP NOT NULL DEFAULT NOW(),
    synced_at TIMESTAMP
);

CREATE INDEX idx_sync_records_tenant_user ON sync_records(tenant_id, user_id);
CREATE INDEX idx_sync_records_status ON sync_records(status);
CREATE INDEX idx_sync_records_entity ON sync_records(entity_type, server_id);
CREATE INDEX idx_sync_records_pending ON sync_records(tenant_id, user_id, status) 
    WHERE status IN ('PENDING', 'FAILED');

-- ============================================================
-- AI ANALYSIS MODULE
-- ============================================================

CREATE TABLE ai_image_analyses (
    id BIGSERIAL PRIMARY KEY,
    tenant_id BIGINT NOT NULL REFERENCES tenants(id),
    photo_url VARCHAR(500) NOT NULL,
    inspection_id BIGINT REFERENCES inspections(id),
    hazard_id BIGINT REFERENCES hazards(id),
    analysis_type VARCHAR(50) NOT NULL,  -- HAZARD_DETECTION, PPE_CHECK, CHANGE_DETECTION
    hazards_detected JSONB,
    overall_risk_score DECIMAL(5,2),
    confidence DECIMAL(5,2),
    processing_time_ms BIGINT,
    raw_analysis JSONB,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_ai_analysis_tenant ON ai_image_analyses(tenant_id);
CREATE INDEX idx_ai_analysis_inspection ON ai_image_analyses(inspection_id);
CREATE INDEX idx_ai_analysis_risk ON ai_image_analyses(overall_risk_score) 
    WHERE overall_risk_score >= 70;

-- ============================================================
-- IOT SENSOR MODULE
-- ============================================================

CREATE TABLE iot_devices (
    device_id VARCHAR(100) PRIMARY KEY,
    tenant_id BIGINT NOT NULL REFERENCES tenants(id),
    name VARCHAR(200) NOT NULL,
    location_id BIGINT NOT NULL,
    location_type VARCHAR(20) NOT NULL,
    sensor_types TEXT[] NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ONLINE',
    last_seen TIMESTAMP,
    battery_level INT,
    firmware_version VARCHAR(50),
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE sensor_readings (
    id BIGSERIAL PRIMARY KEY,
    sensor_id VARCHAR(100) NOT NULL REFERENCES iot_devices(device_id),
    sensor_type VARCHAR(50) NOT NULL,
    location_id BIGINT NOT NULL,
    location_type VARCHAR(20) NOT NULL,
    value DECIMAL(15, 5) NOT NULL,
    unit VARCHAR(20) NOT NULL,
    timestamp TIMESTAMP NOT NULL DEFAULT NOW(),
    metadata JSONB
);

CREATE TABLE sensor_thresholds (
    id BIGSERIAL PRIMARY KEY,
    sensor_type VARCHAR(50) NOT NULL,
    location_id BIGINT NOT NULL,
    warning_min DECIMAL(15, 5),
    warning_max DECIMAL(15, 5),
    critical_min DECIMAL(15, 5),
    critical_max DECIMAL(15, 5),
    unit VARCHAR(20) NOT NULL,
    UNIQUE(sensor_type, location_id)
);

CREATE TABLE geofences (
    id VARCHAR(100) PRIMARY KEY,
    tenant_id BIGINT NOT NULL REFERENCES tenants(id),
    name VARCHAR(200) NOT NULL,
    location_id BIGINT NOT NULL,
    zone_type VARCHAR(50) NOT NULL,
    coordinates JSONB NOT NULL,
    authorized_roles TEXT[],
    requires_escort BOOLEAN DEFAULT FALSE,
    max_occupancy INT
);

CREATE TABLE personnel_locations (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES app_user(id),
    device_id VARCHAR(100),
    latitude DECIMAL(10, 8) NOT NULL,
    longitude DECIMAL(11, 8) NOT NULL,
    altitude DECIMAL(10, 2),
    accuracy DECIMAL(10, 2),
    timestamp TIMESTAMP NOT NULL DEFAULT NOW(),
    current_zone VARCHAR(100),
    is_in_restricted_zone BOOLEAN DEFAULT FALSE
);

CREATE INDEX idx_sensor_readings_sensor ON sensor_readings(sensor_id, timestamp DESC);
CREATE INDEX idx_sensor_readings_location ON sensor_readings(location_id, sensor_type, timestamp DESC);
CREATE INDEX idx_personnel_locations_user ON personnel_locations(user_id, timestamp DESC);
CREATE INDEX idx_personnel_locations_time ON personnel_locations(timestamp);

-- ============================================================
-- GAMIFICATION MODULE
-- ============================================================

CREATE TABLE safety_scorecards (
    user_id BIGINT NOT NULL,
    tenant_id BIGINT NOT NULL REFERENCES tenants(id),
    total_score INT NOT NULL DEFAULT 0,
    level INT NOT NULL DEFAULT 1,
    rank INT,
    team_rank INT,
    shift_rank INT,
    current_streak_count INT DEFAULT 0,
    current_streak_type VARCHAR(50),
    longest_streak_count INT DEFAULT 0,
    longest_streak_type VARCHAR(50),
    last_updated TIMESTAMP NOT NULL DEFAULT NOW(),
    PRIMARY KEY (user_id, tenant_id)
);

CREATE TABLE badges (
    id VARCHAR(100) NOT NULL,
    user_id BIGINT NOT NULL,
    tenant_id BIGINT NOT NULL,
    name VARCHAR(200) NOT NULL,
    description TEXT,
    icon_url VARCHAR(500),
    rarity VARCHAR(20) NOT NULL,
    category VARCHAR(50) NOT NULL,
    earned_at TIMESTAMP NOT NULL DEFAULT NOW(),
    PRIMARY KEY (id, user_id, tenant_id)
);

CREATE TABLE points_transactions (
    id VARCHAR(100) PRIMARY KEY,
    user_id BIGINT NOT NULL,
    tenant_id BIGINT NOT NULL,
    points INT NOT NULL,
    type VARCHAR(50) NOT NULL,
    description TEXT,
    reference_id VARCHAR(100),
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE safety_challenges (
    id VARCHAR(100) PRIMARY KEY,
    tenant_id BIGINT NOT NULL REFERENCES tenants(id),
    name VARCHAR(200) NOT NULL,
    description TEXT,
    type VARCHAR(20) NOT NULL,
    metric VARCHAR(50) NOT NULL,
    target_value INT NOT NULL,
    start_date TIMESTAMP NOT NULL,
    end_date TIMESTAMP NOT NULL,
    rewards_points INT NOT NULL,
    rewards_badge VARCHAR(100),
    rewards_title VARCHAR(200),
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE'
);

CREATE TABLE challenge_participants (
    challenge_id VARCHAR(100) NOT NULL REFERENCES safety_challenges(id),
    user_id BIGINT NOT NULL,
    current_score INT DEFAULT 0,
    rank INT,
    joined_at TIMESTAMP NOT NULL DEFAULT NOW(),
    PRIMARY KEY (challenge_id, user_id)
);

CREATE INDEX idx_scorecards_tenant ON safety_scorecards(tenant_id, total_score DESC);
CREATE INDEX idx_badges_user ON badges(user_id, tenant_id);
CREATE INDEX idx_points_user ON points_transactions(user_id, tenant_id, created_at DESC);
CREATE INDEX idx_challenges_tenant ON safety_challenges(tenant_id, status);

-- ============================================================
-- EMERGENCY RESPONSE MODULE
-- ============================================================

CREATE TABLE emergencies (
    id VARCHAR(100) PRIMARY KEY,
    tenant_id BIGINT NOT NULL REFERENCES tenants(id),
    type VARCHAR(50) NOT NULL,  -- FIRE, GAS_LEAK, CAVE_IN, etc.
    severity VARCHAR(20) NOT NULL,
    location_id BIGINT NOT NULL,
    location_type VARCHAR(20) NOT NULL,
    latitude DECIMAL(10, 8),
    longitude DECIMAL(11, 8),
    reported_by BIGINT REFERENCES app_user(id),
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    description TEXT,
    started_at TIMESTAMP NOT NULL DEFAULT NOW(),
    resolved_at TIMESTAMP
);

CREATE TABLE emergency_responses (
    id BIGSERIAL PRIMARY KEY,
    emergency_id VARCHAR(100) NOT NULL REFERENCES emergencies(id),
    responder_id BIGINT REFERENCES app_user(id),
    response_type VARCHAR(50) NOT NULL,  -- DISPATCHED, ARRIVED, ACTION_TAKEN
    notes TEXT,
    timestamp TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE muster_points (
    id VARCHAR(100) PRIMARY KEY,
    tenant_id BIGINT NOT NULL REFERENCES tenants(id),
    name VARCHAR(200) NOT NULL,
    location_id BIGINT NOT NULL,
    latitude DECIMAL(10, 8) NOT NULL,
    longitude DECIMAL(11, 8) NOT NULL,
    capacity INT,
    is_active BOOLEAN DEFAULT TRUE
);

CREATE TABLE muster_attendance (
    id BIGSERIAL PRIMARY KEY,
    emergency_id VARCHAR(100) NOT NULL REFERENCES emergencies(id),
    user_id BIGINT NOT NULL REFERENCES app_user(id),
    muster_point_id VARCHAR(100) REFERENCES muster_points(id),
    checked_in_at TIMESTAMP NOT NULL DEFAULT NOW(),
    is_accounted_for BOOLEAN DEFAULT TRUE
);

CREATE INDEX idx_emergencies_tenant ON emergencies(tenant_id, status);
CREATE INDEX idx_emergencies_active ON emergencies(status) WHERE status = 'ACTIVE';
CREATE INDEX idx_muster_attendance ON muster_attendance(emergency_id, user_id);

-- ============================================================
-- INSERT DEFAULT THRESHOLDS
-- ============================================================

INSERT INTO sensor_thresholds (sensor_type, location_id, warning_min, warning_max, critical_min, critical_max, unit) VALUES
-- Gas detection
('METHANE', 0, NULL, 1.0, NULL, 2.5, '% LEL'),
('CARBON_MONOXIDE', 0, NULL, 35.0, NULL, 50.0, 'ppm'),
('HYDROGEN_SULFIDE', 0, NULL, 10.0, NULL, 20.0, 'ppm'),
('OXYGEN_LEVEL', 0, 19.5, NULL, 18.0, NULL, '%'),
-- Environmental
('DUST_PARTICULATE', 0, NULL, 150.0, NULL, 250.0, 'μg/m³'),
('NOISE_LEVEL', 0, NULL, 85.0, NULL, 100.0, 'dB'),
('TEMPERATURE', 0, NULL, 35.0, NULL, 40.0, '°C');
