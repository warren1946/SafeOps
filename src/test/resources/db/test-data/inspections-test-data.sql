-- Test data for inspection integration tests

-- Clean existing test data
DELETE FROM inspection_items WHERE inspection_id IN (SELECT id FROM inspections WHERE title LIKE 'Test%');
DELETE FROM inspections WHERE title LIKE 'Test%';

-- Insert test inspections
INSERT INTO inspections (id, title, target_type, target_id, inspector_id, assigned_reviewer_id, reviewer_comments, status, created_at, updated_at)
VALUES 
    (1, 'Test Inspection - Area B', 'AREA', 1, 1, NULL, NULL, 'DRAFT', NOW(), NOW()),
    (2, 'Test Inspection - Shaft A', 'SHAFT', 1, 1, NULL, NULL, 'DRAFT', NOW(), NOW()),
    (3, 'Test Inspection - Site 1', 'SITE', 1, 2, NULL, NULL, 'SUBMITTED', NOW(), NOW());

-- Insert test inspection items for inspection 2 (so it can be submitted)
INSERT INTO inspection_items (id, inspection_id, title, status, comment, created_at)
VALUES 
    (1, 2, 'Check ventilation system', 'PASS', 'All clear', NOW()),
    (2, 2, 'Check lighting', 'PASS', 'Working properly', NOW()),
    (3, 2, 'Check emergency exits', 'PASS', 'Accessible', NOW());

-- Reset sequence if needed (PostgreSQL specific)
-- SELECT setval('inspections_id_seq', (SELECT MAX(id) FROM inspections));
-- SELECT setval('inspection_items_id_seq', (SELECT MAX(id) FROM inspection_items));
