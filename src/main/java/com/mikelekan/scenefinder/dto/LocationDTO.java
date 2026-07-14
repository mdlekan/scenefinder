package com.mikelekan.scenefinder.dto;

import lombok.Data;

@Data
public class LocationDTO
{
    private Long id;
    private String name;
    private String description;
    private Integer elevationFt;
    private String bestSeason;
    private String bestTimeOfDay;
    private String accessNotes;
    private String[] tags;
    private String difficulty;
    private String parkingNotes;
    private Boolean permitRequired;
    private String permitNotes;
    private String region;
    private double latitude;
    private double longitude;
    private String sunriseTime;
    private String morningGoldenHour;
    private String eveningGoldenHour;
    private String solarNoonTime;
    private String todaySunrise;
    private String todayMorningGoldenHour;
    private String todayEveningGoldenHour;
    private String todaySolarNoon;
    private String todaySunset;
}