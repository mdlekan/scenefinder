CREATE TABLE IF NOT EXISTS photos (
                                      id              BIGSERIAL PRIMARY KEY,
                                      location_id     BIGINT NOT NULL REFERENCES locations(id) ON DELETE CASCADE,
    url             TEXT NOT NULL,
    credit          VARCHAR(255),
    caption         TEXT,
    orientation     VARCHAR(20) DEFAULT 'landscape',
    is_primary      BOOLEAN DEFAULT FALSE,
    created_at      TIMESTAMP DEFAULT NOW()
    );

CREATE INDEX IF NOT EXISTS idx_photos_location ON photos(location_id);