package com.mikelekan.scenefinder.model;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import java.time.Instant;

@Entity
@Data
@Builder
@Table(name = "users")
public class User
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    private String email;
    private String passwordHash;
    private Instant createdAt;
}
