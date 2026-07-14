package com.mikelekan.scenefinder.dto;

import java.time.ZonedDateTime;

public record SunTimesResultDTO(
        ZonedDateTime sunrise,
        ZonedDateTime sunset,
        ZonedDateTime morningGoldenHourEnd,
        ZonedDateTime eveningGoldenHourStart)
{ }
