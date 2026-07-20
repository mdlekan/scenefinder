INSERT INTO locations (name, description, geom, elevation_ft, best_season, best_time_of_day, tags)
VALUES
    ('Maroon Bells', 'Iconic twin peaks reflected in Maroon Lake near Aspen.',
     ST_SetSRID(ST_MakePoint(-106.9390, 39.0708), 4326), 9580,
     'Fall', 'Morning', ARRAY['mountains', 'lake', 'reflection', 'iconic']),

    ('Great Sand Dunes', 'Towering dunes against the Sangre de Cristo Mountains.',
     ST_SetSRID(ST_MakePoint(-105.5943, 37.7916), 4326), 8200,
     'Spring', 'Golden hour', ARRAY['dunes', 'desert', 'mountains', 'dramatic']),

    ('Black Canyon of the Gunnison', 'Deep dramatic canyon with sheer dark walls.',
     ST_SetSRID(ST_MakePoint(-107.7242, 38.5754), 4326), 8150,
     'Summer', 'Sunset', ARRAY['canyon', 'dramatic', 'geology']),

    ('Rocky Mountain National Park - Bear Lake', 'Stunning alpine lake surrounded by peaks.',
     ST_SetSRID(ST_MakePoint(-105.6457, 40.3121), 4326), 9475,
     'Summer', 'Morning', ARRAY['alpine', 'lake', 'peaks', 'wildlife']),

    ('Mesa Verde', 'Ancient cliff dwellings with sweeping canyon views.',
     ST_SetSRID(ST_MakePoint(-108.4618, 37.1853), 4326), 7000,
     'Spring', 'Midday', ARRAY['history', 'canyon', 'southwest']);