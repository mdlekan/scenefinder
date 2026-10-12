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

    public List<LocationNoteResponseDTO> getNotesForLocation(Long locationId) {
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

    public Map<String, Long> keywordFrequency() {
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

    public List<String> topFiveWords() {
        return locationNoteRepository.findAll().stream()
                .filter(note -> note.getNote() != null && !note.getNote().isEmpty())
                .flatMap(note -> Arrays.stream(note.getNote().split("\\s+")))
                .map(word -> word.replaceAll("[^a-zA-Z]", "").toLowerCase())
                .filter(word -> word.length() > 3)
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
                .entrySet().stream()
                .sorted((a, b) -> b.getValue().compareTo(a.getValue()))  // sort by count, descending
                .limit(5)                                                       // keep the first five
                .map(Map.Entry::getKey)                                         // entry -> just the word
                .collect(Collectors.toList());                                  // gather into a List<String>
    }

//    public Map<String, Long> notesPerUserName()
//    {
//        return locationNoteRepository.findAll().stream()
//                .
//    }


    /*

   1 Return the top 5 keywords as a List<String>, without the counts.
   2 Count the notes per username, as a Map<String, Long>.
   3 Find the average sunriseDelayMinutes per location ID, skipping notes where it's null.
   4 Split the notes into two groups: those with a sunset delay over 15 minutes and the rest.
   5 Find the note with the longest text. What should the return type be, and why?
   6 Build a Map<String, List<String>> of username to their note texts, with each list ordered by createdAt.
   7 Get the most recent note per location, as a Map<Long, LocationNote>. There are at least two ways to do this.
   8 Get the min, max, and average sunset delay in a single pass.
   9 Return the usernames who have written notes for more than one distinct location.
   10 Given a String, find the first non-repeated character. This one is outside your domain, but it's a common interview question.

     */
}