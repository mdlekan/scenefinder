package com.mikelekan.scenefinder.repository;

import com.mikelekan.scenefinder.model.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface LocationRepository extends JpaRepository<Location, Long>
{
    // Find all locations within X meters of a given point
    @Query(value = """
        SELECT * FROM locations
        WHERE ST_DWithin(
            geom::geography,
            ST_SetSRID(ST_MakePoint(:lng, :lat), 4326)::geography,
            :radiusMeters
        )
        ORDER BY ST_Distance(
            geom::geography,
            ST_SetSRID(ST_MakePoint(:lng, :lat), 4326)::geography
        )
        """, nativeQuery = true)

    List<Location> findWithinRadius(
            @Param("lat") double lat,
            @Param("lng") double lng,
            @Param("radiusMeters") double radiusMeters
    );
}