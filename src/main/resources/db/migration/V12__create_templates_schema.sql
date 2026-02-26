CREATE TABLE templates (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    category VARCHAR(100),
    created_by BIGINT,
    usage_count INT NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_template_created_by
        FOREIGN KEY (created_by) REFERENCES app_user(id)
);

CREATE TABLE template_questions (
    id BIGSERIAL PRIMARY KEY,
    template_id BIGINT NOT NULL,
    text TEXT NOT NULL,
    type VARCHAR(50) NOT NULL,
    required BOOLEAN NOT NULL,
    order_index INT NOT NULL,
    options JSONB NULL,
    CONSTRAINT fk_template_questions_template
        FOREIGN KEY (template_id) REFERENCES templates(id)
);

CREATE INDEX idx_template_questions_template_id ON template_questions(template_id);