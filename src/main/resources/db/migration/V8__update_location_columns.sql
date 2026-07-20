ALTER TABLE locations ALTER COLUMN permit_required TYPE VARCHAR(20);
ALTER TABLE locations ALTER COLUMN region TYPE VARCHAR(250);
ALTER TABLE locations ALTER COLUMN difficulty TYPE VARCHAR(250);
ALTER TABLE locations ALTER COLUMN parking_notes TYPE VARCHAR(250);
ALTER TABLE locations ALTER COLUMN permit_notes TYPE VARCHAR(250);
ALTER TABLE locations ALTER COLUMN solar_noon_time TYPE VARCHAR(255);

ALTER TABLE locations DROP COLUMN IF EXISTS sunrise_time;
ALTER TABLE locations DROP COLUMN IF EXISTS sunset_time;
ALTER TABLE locations DROP COLUMN IF EXISTS morning_golden_hour;
ALTER TABLE locations DROP COLUMN IF EXISTS evening_golden_hour;