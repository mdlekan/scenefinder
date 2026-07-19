package com.mikelekan.scenefinder.dto;

import lombok.*;
import java.time.OffsetDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LocationNoteResponseDTO
{
    private Long id;
    private String note;
    private Integer sunriseDelayMinutes;
    private Integer sunsetDelayMinutes;
    private String username;
    private OffsetDateTime createdAt;
}
