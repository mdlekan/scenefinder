package com.mikelekan.scenefinder.service;

import com.mikelekan.scenefinder.dto.LocationNoteRequestDTO;
import com.mikelekan.scenefinder.dto.LocationNoteResponseDTO;
import com.mikelekan.scenefinder.model.Location;
import com.mikelekan.scenefinder.model.LocationNote;
import com.mikelekan.scenefinder.model.User;
import com.mikelekan.scenefinder.repository.LocationNoteRepository;
import com.mikelekan.scenefinder.repository.LocationRepository;
import com.mikelekan.scenefinder.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class LocationNoteService {

    private final LocationNoteRepository locationNoteRepository;
    private final LocationRepository locationRepository;
    private final UserRepository userRepository;

    public LocationNoteService(LocationNoteRepository locationNoteRepository,
                               LocationRepository locationRepository,
                               UserRepository userRepository) {
        this.locationNoteRepository = locationNoteRepository;
        this.locationRepository = locationRepository;
        this.userRepository = userRepository;
    }

    public List<LocationNoteResponseDTO> getNotesForLocation(Long locationId)
    {
        return locationNoteRepository.findByLocationIdAndIsCurrentTrue(locationId).stream()
                .map(note -> LocationNoteResponseDTO.builder()
                        .id(note.getId())
                        .note(note.getNote())
                        .sunriseDelayMinutes(note.getSunriseDelayMinutes())
                        .sunsetDelayMinutes(note.getSunsetDelayMinutes())
                        .username(note.getUser().getUsername())
                        .createdAt(note.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
    }

    public void addNote(Long locationId, String username, LocationNoteRequestDTO request) {
        Location location = locationRepository.findById(locationId)
                .orElseThrow(() -> new RuntimeException("Location not found"));

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        LocationNote note = LocationNote.builder()
                .location(location)
                .user(user)
                .note(request.getNote())
                .sunriseDelayMinutes(request.getSunriseDelayMinutes())
                .sunsetDelayMinutes(request.getSunsetDelayMinutes())
                .build();

        locationNoteRepository.save(note);
    }

    public Map<String, Long> keywordFrequency()
    {
        Map<String, Long> rawCounts = locationNoteRepository.findAll().stream()
                .filter(note -> note.getNote() != null && !note.getNote().isEmpty())
                .flatMap(note -> Arrays.stream(note.getNote().split("\\s+")))
                .map(word -> word.replaceAll("[^a-zA-Z]", "").toLowerCase()) // added toLowerCase() so "Sunrise" and "sunrise" match!
                .filter(word -> word.length() > 3)
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        return rawCounts.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));
    }
}