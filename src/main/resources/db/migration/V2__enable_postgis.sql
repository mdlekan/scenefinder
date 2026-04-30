CREATE TABLE IF NOT EXISTS locations (
                                         id              BIGSERIAL PRIMARY KEY,
                                         name            VARCHAR(255) NOT NULL,
    description     TEXT,
    geom            GEOMETRY(Point, 4326) NOT NULL,
    elevation_ft    INTEGER,
    best_season     VARCHAR(50),
    best_time_of_day VARCHAR(50),
    access_notes    TEXT,
    difficulty      VARCHAR(50),
    parking_notes   TEXT,
    permit_required BOOLEAN DEFAULT FALSE,
    permit_notes    TEXT,
    region          VARCHAR(100),
    tags            TEXT[],
    created_at      TIMESTAMP DEFAULT NOW(),
    updated_at      TIMESTAMP DEFAULT NOW()
    );

CREATE INDEX IF NOT EXISTS idx_locations_geom ON locations USING GIST(geom);