package com.mikelekan.scenefinder.service;

import com.mikelekan.scenefinder.dto.LocationDTO;
import com.mikelekan.scenefinder.model.Location;
import com.mikelekan.scenefinder.repository.LocationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.PrecisionModel;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.locationtech.jts.geom.Point;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LocationServiceTest
{
    @Mock
    private LocationRepository locationRepository;
    @Mock
    private SunCalculatorService sunCalculatorService;

    @InjectMocks
    private LocationService locationService;


    private List<Location> locationList;

    @BeforeEach
    void setUp()
    {
        Location location = new Location();

       location.setId(101L);
       location.setName("Test Location");
       location.setBestSeason("Spring");
       location.setTags(new String[]{"Mountains"});
        GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);
        Point point = geometryFactory.createPoint(new Coordinate(-105.5, 39.0));
        location.setGeom(point);

       locationList = new ArrayList<>();
       locationList.add(location);

    }

    @Test
    public void testGetAllLocations()
    {
        when(locationRepository.findAll()).thenReturn(locationList);

        List<LocationDTO> result = locationService.getAllLocations("Spring", "Mountains");

        assertThat(result.size()).isEqualTo(1);
        assertThat(result.getFirst().getId()).isEqualTo(101L);
    }

    @Test
    public void testGetAllLocationsSeasonNoMatch()
    {
        when(locationRepository.findAll()).thenReturn(locationList);

        List<LocationDTO> result = locationService.getAllLocations("Winter", "Mountains");

        assertThat(result.size()).isEqualTo(0);
    }

    @Test
    public void testGetAllLocationsTagNoMatch()
    {
        when(locationRepository.findAll()).thenReturn(locationList);

        List<LocationDTO> result = locationService.getAllLocations("Spring", "desert");

        assertThat(result.size()).isEqualTo(0);
    }

    @Test
    public void testGetNearbyLocations()
    {
        when(locationRepository.findWithinRadius(-105.5, 39.0, 100_000d)).thenReturn(locationList);

        List<LocationDTO> result = locationService.getNearbyLocations(-105.5, 39.0, 100);
        assertThat(result.size()).isEqualTo(1);
    }

    @Test
    public void testGetNearbyLocations_noResultsWithinRadius()
    {
        when(locationRepository.findWithinRadius(-105.5, 39.0, 100_000d))
                .thenReturn(new ArrayList<>());

        List<LocationDTO> result = locationService.getNearbyLocations(-105.5, 39.0, 100);

        assertThat(result.size()).isEqualTo(0);
    }

    @Test
    public void testSunCalculator()
    {
        when(locationRepository.findAll()).thenReturn(locationList);
        when(sunCalculatorService.calculateSunTimes(39.0, -105.5)).thenReturn(new HashMap<>(Map.of(
            "eveningGoldenHour","4:55")));

        List<LocationDTO> result = locationService.getAllLocations("Spring", "Mountains");

        assertThat(result.size()).isEqualTo(1);
        assertThat(result.getFirst().getId()).isEqualTo(101L);
        assertThat(result.getFirst().getTodayEveningGoldenHour()).isEqualTo("4:55");
    }
}

