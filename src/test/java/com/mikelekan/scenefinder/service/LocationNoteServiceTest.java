package com.mikelekan.scenefinder.service;

import com.mikelekan.scenefinder.dto.LocationNoteRequestDTO;
import com.mikelekan.scenefinder.dto.LocationNoteResponseDTO;
import com.mikelekan.scenefinder.model.Location;
import com.mikelekan.scenefinder.model.LocationNote;
import com.mikelekan.scenefinder.model.User;
import com.mikelekan.scenefinder.repository.LocationNoteRepository;
import com.mikelekan.scenefinder.repository.LocationRepository;
import com.mikelekan.scenefinder.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LocationNoteServiceTest {

    @Mock
    private LocationNoteRepository locationNoteRepository;

    @Mock
    private LocationRepository locationRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private LocationNoteService locationNoteService;

    private Location testLocation;
    private User testUser;

    @BeforeEach
    void setUp() {
        testLocation = new Location();
        testLocation.setId(100L);
        testLocation.setName("Maroon Bells Viewpoint");

        testUser = User.builder()
                .id(1L)
                .username("painter_dan")
                .email("dan@example.com")
                .build();
    }

    @Test
    @DisplayName("addNote - Should successfully build and persist LocationNote")
    void addNote_Success() {
        // Arrange
        LocationNoteRequestDTO request = new LocationNoteRequestDTO();
        request.setNote("Best light is 20 mins before sunrise due to shadow from Peak A.");
        request.setSunriseDelayMinutes(-20);
        request.setSunsetDelayMinutes(0);

        when(locationRepository.findById(100L)).thenReturn(Optional.of(testLocation));
        when(userRepository.findByUsername("painter_dan")).thenReturn(Optional.of(testUser));

        // Act
        locationNoteService.addNote(100L, "painter_dan", request);

        // Assert
        ArgumentCaptor<LocationNote> noteCaptor = ArgumentCaptor.forClass(LocationNote.class);
        verify(locationNoteRepository, times(1)).save(noteCaptor.capture());

        LocationNote savedNote = noteCaptor.getValue();
        assertThat(savedNote.getNote()).isEqualTo("Best light is 20 mins before sunrise due to shadow from Peak A.");
        assertThat(savedNote.getSunriseDelayMinutes()).isEqualTo(-20);
        assertThat(savedNote.getLocation()).isEqualTo(testLocation);
        assertThat(savedNote.getUser()).isEqualTo(testUser);
    }

    @Test
    @DisplayName("addNote - Should throw RuntimeException when location does not exist")
    void addNote_LocationNotFound_ThrowsException() {
        // Arrange
        LocationNoteRequestDTO request = new LocationNoteRequestDTO();
        when(locationRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> locationNoteService.addNote(999L, "painter_dan", request))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Location not found");

        verify(locationNoteRepository, never()).save(any());
    }

    @Test
    @DisplayName("getNotesForLocation - Should map entities to response DTOs")
    void getNotesForLocation_Success() {
        // Arrange
        LocationNote note = LocationNote.builder()
                .id(1L)
                .note("Great morning spot")
                .sunriseDelayMinutes(10)
                .sunsetDelayMinutes(5)
                .user(testUser)
                .createdAt(OffsetDateTime.now())
                .build();

        when(locationNoteRepository.findByLocationIdAndIsCurrentTrue(100L))
                .thenReturn(List.of(note));

        // Act
        List<LocationNoteResponseDTO> results = locationNoteService.getNotesForLocation(100L);

        // Assert
        assertThat(results).hasSize(1);
        assertThat(results.getFirst().getUsername()).isEqualTo("painter_dan");
        assertThat(results.getFirst().getNote()).isEqualTo("Great morning spot");
    }

    @Test
    @DisplayName("count occurances of words in a note")
    void keywordFrequencyTest()
    {
        LocationNote note = LocationNote.builder()
                .id(1L)
                .note("Great morning spot for mostly intermediat to advanced")
                .sunriseDelayMinutes(10)
                .sunsetDelayMinutes(5)
                .user(testUser)
                .createdAt(OffsetDateTime.now())
                .build();

        LocationNote note2 = LocationNote.builder()
                .id(2L)
                .note("Great evening spot to try for beginners")
                .sunriseDelayMinutes(10)
                .sunsetDelayMinutes(5)
                .user(testUser)
                .createdAt(OffsetDateTime.now())
                .build();

        List<LocationNote> locationNotes = new ArrayList<>();
        locationNotes.add(note);
        locationNotes.add(note2);

        when(locationNoteRepository.findAll()).thenReturn(locationNotes);

        Map<String, Long>  result = locationNoteService.keywordFrequency();

        assertNotNull(result);
        assertEquals(2L, result.get("great"));
        assertEquals(2L, result.get("spot"));
        assertFalse(result.containsKey("to"));
        assertFalse(result.containsKey("for"));

        // Verify top elements order
        List<String> keys = new ArrayList<>(result.keySet());
        assertEquals("great", keys.get(0));


    }
}