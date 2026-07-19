package com.mikelekan.scenefinder.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class LocationNoteRequestDTO
{
    private String note;
    private Integer sunriseDelayMinutes;
    private Integer sunsetDelayMinutes;
}
