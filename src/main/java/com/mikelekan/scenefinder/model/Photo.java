package com.mikelekan.scenefinder.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "photos")
public class Photo
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id", nullable = false)
    private Location location;

    @Column(nullable = false)
    private String url;

    private String credit;
    private String caption;
    private String orientation;
    private Boolean isPrimary;
    private LocalDateTime createdAt;

}
