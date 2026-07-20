CREATE TABLE IF NOT EXISTS location_notes (
                                              id BIGSERIAL PRIMARY KEY,
                                              location_id BIGINT NOT NULL REFERENCES locations(id) ON DELETE CASCADE,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    note TEXT,
    sunrise_delay_minutes INTEGER,
    sunset_delay_minutes INTEGER,
    is_current BOOLEAN DEFAULT true,
    created_at TIMESTAMPTZ DEFAULT NOW()
    );