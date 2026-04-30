ALTER TABLE locations
    ADD COLUMN IF NOT EXISTS weather_notes TEXT;