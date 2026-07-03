ALTER TABLE churches ADD COLUMN type VARCHAR(20) NOT NULL DEFAULT 'CAPELA';

UPDATE churches SET type = 'MATRIZ' WHERE name = 'Igreja Sede';
