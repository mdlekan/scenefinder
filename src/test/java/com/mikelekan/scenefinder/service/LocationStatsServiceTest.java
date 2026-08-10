package com.mikelekan.scenefinder.service;

import com.mikelekan.scenefinder.model.Location;
import com.mikelekan.scenefinder.repository.LocationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.List;
import java.util.Map;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LocationStatsServiceTest
{
    @Mock
    private LocationRepository locationRepository;

    @InjectMocks
    private LocationStatsService locationStatsService;

    private Location loc1;
    private Location loc2;
    private Location loc3;

    @BeforeEach
    void setUp() {
        loc1 = new Location();
        loc1.setId(1L);
        loc1.setName("Golden Gate Overlook");
        loc1.setRegion("San Francisco");
        loc1.setDifficulty("Easy");
        loc1.setBestSeason("Spring");
        loc1.setElevationFt(300);

        loc2 = new Location();
        loc2.setId(2L);
        loc2.setName("Alpine Lake");
        loc2.setRegion("Rockies");
        loc2.setDifficulty("Moderate");
        loc2.setBestSeason("Summer");
        loc2.setElevationFt(9500);

        loc3 = new Location();
        loc3.setId(3L);
        loc3.setName("Bear Lake View");
        loc3.setRegion("Rockies");
        loc3.setDifficulty("Easy");
        loc3.setBestSeason("Spring");
        loc3.setElevationFt(9400);
    }

    @Test
    @DisplayName("countsByRegion - Should aggregate locations correctly by region")
    void countsByRegion() {
        when(locationRepository.findAll()).thenReturn(List.of(loc1, loc2, loc3));

        Map<String, Long> result = locationStatsService.countsByRegion();

        assertThat(result)
                .containsEntry("San Francisco", 1L)
                .containsEntry("Rockies", 2L);
    }

    @Test
    @DisplayName("easyLocationNamesBySeason - Should filter easy locations and sort names")
    void easyLocationNamesBySeason() {
        when(locationRepository.findAll()).thenReturn(List.of(loc1, loc2, loc3));

        Map<String, List<String>> result = locationStatsService.easyLocationNamesBySeason();

        assertThat(result).containsKey("Spring");
        // Bear Lake View and Golden Gate Overlook are both Easy and Spring
        assertThat(result.get("Spring")).containsExactly("Bear Lake View", "Golden Gate Overlook");
    }

    @Test
    @DisplayName("topNByElevation - Should return highest elevation locations sorted descending")
    void topNByElevation() {
        when(locationRepository.findAll()).thenReturn(List.of(loc1, loc2, loc3));

        List<Location> top2 = locationStatsService.topNByElevation(2);

        assertThat(top2).hasSize(2);
        assertThat(top2.get(0).getName()).isEqualTo("Alpine Lake");      // 9,500 ft
        assertThat(top2.get(1).getName()).isEqualTo("Bear Lake View");   // 9,400 ft
    }
}
