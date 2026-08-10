package com.mikelekan.scenefinder.controller;

import com.mikelekan.scenefinder.service.LocationStatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

@Controller
@RequestMapping("/api/locationstats")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class LocationStatsController
{
    private final LocationStatsService locationStatsService;

    @GetMapping("/by-region")
    public Map<String, Long> countByRegion()
    {
        return locationStatsService.countsByRegion();
    }

    @GetMapping("/tag-frequency")
    public Map<String, Long> countByTagFrequency()
    {
        return null;
    }
}
