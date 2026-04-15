package com.mikelekan.scenefinder.dto;

import lombok.Data;

@Data
public class LocationDTO {
    private Long id;
    private String name;
    private String description;
    private double latitude;
    private double longitude;
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
}