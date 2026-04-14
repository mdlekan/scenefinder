package com.mikelekan.scenefinder.controller;

import com.mikelekan.scenefinder.dto.LocationDTO;
import com.mikelekan.scenefinder.service.LocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/locations")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class LocationController
{
    private final LocationService locationService;

    @GetMapping
    public List<LocationDTO> getAllLocations(@RequestParam(required = false) String season,
                                             @RequestParam(required = false) String tag)
    {
        return locationService.getAllLocations(season, tag);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LocationDTO> getLocationById(@PathVariable Long id)
    {
        return locationService.getLocationById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/nearby")
    public List<LocationDTO> getNearbyLocations(
            @RequestParam double lat,
            @RequestParam double lng,
            @RequestParam(defaultValue = "50") double radius)
    {
        return locationService.getNearbyLocations(lat, lng, radius);
    }

}
