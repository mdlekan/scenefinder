package com.mikelekan.scenefinder.service;

import com.mikelekan.scenefinder.dto.LocationDTO;
import com.mikelekan.scenefinder.model.Location;
import com.mikelekan.scenefinder.repository.LocationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class LocationService
{
    private final LocationRepository locationRepository;

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

    public Optional<LocationDTO> getLocationById(Long id) {

        return locationRepository.findById(id).map(this::toDTO);
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
}
