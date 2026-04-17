package com.mikelekan.scenefinder.service;

import com.mikelekan.scenefinder.dto.LocationDTO;
import com.mikelekan.scenefinder.repository.LocationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith; // Add this
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension; // Add this

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class) // This is the missing piece!
public class TestLocationService {

    @Mock
    LocationRepository locationRepository;

    @InjectMocks
    LocationService locationService;

    @Test
    public void testLocationDTOToLocation() {
        // Arrange
        LocationDTO locationDTO = new LocationDTO();
        locationDTO.setDescription("Tests");
        locationDTO.setLatitude(32.98);
        locationDTO.setLongitude(34.77);

        // Act
        // This calls the REAL method in your LocationService
        var result = locationService.locationDTOToLocation(locationDTO);

        // Assert
        // Now you should verify that the conversion actually worked
        assertEquals("Tests", result.getDescription());
       // assertEquals(32.98, result.getLatitude());
    }
}