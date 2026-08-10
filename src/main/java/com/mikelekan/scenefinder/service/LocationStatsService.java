package com.mikelekan.scenefinder.service;

import com.mikelekan.scenefinder.model.Location;
import com.mikelekan.scenefinder.model.LocationNote;
import com.mikelekan.scenefinder.repository.LocationNoteRepository;
import com.mikelekan.scenefinder.repository.LocationRepository;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class LocationStatsService
{
    private final LocationRepository locationRepository;
    private final LocationNoteRepository locationNoteRepository;

    public LocationStatsService(LocationRepository locationRepository, LocationNoteRepository locationNoteRepository)
    {
        this.locationRepository = locationRepository;
        this.locationNoteRepository = locationNoteRepository;
    }

    public Map<String, Long> countsByRegion()
    {
        return locationRepository.findAll().stream()
                .filter(loc -> loc.getRegion() != null)
                .collect(Collectors.groupingBy(Location::getRegion, Collectors.counting()));
    }

    public Map<String, Double> averageElevationByDifficulty()
    {
        return locationRepository.findAll().stream()
                .filter(loc -> loc.getDifficulty() != null && loc.getElevationFt() != null)
                .collect(Collectors.groupingBy(
                        Location::getDifficulty,
                        Collectors.averagingInt(Location::getElevationFt)));
    }

    public Map<String, Long> tagFreqency()
    {
        Map<String, Long> rawCounts = locationRepository.findAll().stream()
                .filter(loc -> loc.getTags() != null)
                .flatMap(loc -> Arrays.stream(loc.getTags()))
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));


        return rawCounts.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));
    }



    public List<Location> topNByElevation(int n)
    {
        return locationRepository.findAll().stream()
                        .filter(loc -> loc.getElevationFt() != null)
                .sorted(Comparator.comparing(Location::getElevationFt).reversed())
                .limit(n)
                .toList();

        //Collectors.toList() returns a mutable list.
    }

    public Map<String, List<String>> easyLocationNamesBySeason() {
        return locationRepository.findAll().stream()
                .filter(loc -> "Easy".equalsIgnoreCase(loc.getDifficulty()))
                .filter(loc -> loc.getBestSeason() != null)
                .sorted(Comparator.comparing(Location::getName)) // <--- Sort once here
                .collect(Collectors.groupingBy(
                        Location::getBestSeason,
                        Collectors.mapping(Location::getName, Collectors.toList())
                ));
    }

    // yürm,
}
