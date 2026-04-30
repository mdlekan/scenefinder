ALTER TABLE locations
    ADD COLUMN IF NOT EXISTS sunrise_time VARCHAR(10),
    ADD COLUMN IF NOT EXISTS sunset_time VARCHAR(10),
    ADD COLUMN IF NOT EXISTS morning_golden_hour VARCHAR(10),
    ADD COLUMN IF NOT EXISTS evening_golden_hour VARCHAR(10),
    ADD COLUMN IF NOT EXISTS solar_noon_time VARCHAR(10)