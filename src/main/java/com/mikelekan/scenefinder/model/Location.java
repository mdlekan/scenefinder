package com.mikelekan.scenefinder.model;

import jakarta.persistence.*;
import lombok.Data;
import org.locationtech.jts.geom.Point;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "locations")
public class Location {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String description;

    @Column(columnDefinition = "geometry(Point, 4326)", nullable = false)
    private Point geom;

    private Integer elevationFt;
    private String bestSeason;
    private String bestTimeOfDay;
    private String accessNotes;

    @Column(columnDefinition = "text[]")
    private String[] tags;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}