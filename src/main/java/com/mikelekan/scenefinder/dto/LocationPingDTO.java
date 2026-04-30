package com.mikelekan.scenefinder.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record LocationPingDTO(
        @JsonProperty("user_id")        String userId,
        @JsonProperty("location")       String location,
        @JsonProperty("lat")            double lat,
        @JsonProperty("lon")            double lon,
        @JsonProperty("distance_miles") double distanceMiles,
        @JsonProperty("is_local")       boolean isLocal,
        @JsonProperty("timestamp")      double timestamp
) {}