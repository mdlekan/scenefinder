package com.mikelekan.scenefinder.service;

import com.mikelekan.scenefinder.dto.LocationDTO;
import com.mikelekan.scenefinder.model.Location;
import com.mikelekan.scenefinder.repository.LocationRepository;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import org.locationtech.jts.geom.Coordinate;

@Service
public class LocationService
{
    private final LocationRepository locationRepository;

    public LocationService(LocationRepository locationRepository)
    {
        this.locationRepository = locationRepository;
    }

    public List<LocationDTO> getAllLocations(String season, String tag)
    {
        List<Location> locations = locationRepository.findAll();

        return locations.stream()
                .filter(location -> season == null || season.equalsIgnoreCase(location.getBestSeason()))
                .filter(location -> tag == null || (location.getTags() != null &&
                        Stream.of(location.getTags()).anyMatch(t -> t.equalsIgnoreCase(tag))))
                .map(this::toDTO)
                .toList();
    }

    public List<LocationDTO> getNearbyLocations(double lat, double lng, double radiusKm) {
        double radiusMeters = radiusKm * 1000;
        return locationRepository
                .findWithinRadius(lat, lng, radiusMeters)
                .stream()
                .map(this::toDTO)
                .toList();
    }

    public Optional<LocationDTO> getLocationById(Long id)
    {
        return locationRepository.findById(id).map(this::toDTO);
    }

    public LocationDTO addNewLocation(LocationDTO inLocation)
    {
        Location location = locationRepository.save(locationDTOToLocation(inLocation));
        
        return toDTO(location);
    }

    private LocationDTO toDTO(Location location) {
        LocationDTO dto = new LocationDTO();
        dto.setId(location.getId());
        dto.setName(location.getName());
        dto.setDescription(location.getDescription());
        dto.setElevationFt(location.getElevationFt());
        dto.setBestSeason(location.getBestSeason());
        dto.setBestTimeOfDay(location.getBestTimeOfDay());
        dto.setAccessNotes(location.getAccessNotes());
        dto.setTags(location.getTags());
        dto.setDifficulty(location.getDifficulty());
        dto.setParkingNotes(location.getParkingNotes());
        dto.setPermitRequired(location.getPermitRequired());
        dto.setPermitNotes(location.getPermitNotes());
        dto.setRegion(location.getRegion());
        dto.setLatitude(location.getGeom().getY());
        dto.setLongitude(location.getGeom().getX());

        return dto;
    }

    protected Location locationDTOToLocation(LocationDTO inLocationDTO) {
        Location location = new Location();

        // Correct way to create a Point in JTS
        GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);
        Coordinate coordinate = new Coordinate(inLocationDTO.getLongitude(), inLocationDTO.getLatitude());
        Point point = geometryFactory.createPoint(coordinate);

        location.setName(inLocationDTO.getName());
        location.setDescription(inLocationDTO.getDescription());
        location.setElevationFt(inLocationDTO.getElevationFt());
        location.setBestSeason(inLocationDTO.getBestSeason());
        location.setBestTimeOfDay(inLocationDTO.getBestTimeOfDay());
        location.setAccessNotes(inLocationDTO.getAccessNotes());
        location.setTags(inLocationDTO.getTags());
        location.setDifficulty(inLocationDTO.getDifficulty());
        location.setParkingNotes(inLocationDTO.getParkingNotes());
        location.setPermitRequired(inLocationDTO.getPermitRequired());
        location.setPermitNotes(inLocationDTO.getPermitNotes());
        location.setRegion(inLocationDTO.getRegion());
        location.setGeom(point);

        return location;
    }
}
