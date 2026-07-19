package com.mikelekan.scenefinder.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

@Entity
@Table(name = "location_notes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LocationNote
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id", nullable = false)
    private Location location;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private String note;

    private Integer sunriseDelayMinutes;
    private Integer sunsetDelayMinutes;

    @Builder.Default
    private Boolean isCurrent = true;

    @Column(updatable = false)
    private OffsetDateTime createdAt;

    @PrePersist
    public void prePersist()
    {
        createdAt = OffsetDateTime.now();
    }
}
