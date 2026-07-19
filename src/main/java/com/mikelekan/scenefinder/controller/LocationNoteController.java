package com.mikelekan.scenefinder.controller;

import com.mikelekan.scenefinder.dto.LocationDTO;
import com.mikelekan.scenefinder.dto.LocationNoteRequestDTO;
import com.mikelekan.scenefinder.model.Location;
import com.mikelekan.scenefinder.service.LocationNoteService;
import com.mikelekan.scenefinder.service.LocationService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;

@Controller
@RequestMapping("/locations")
public class LocationNoteController {

    private final LocationService locationService;
    private final LocationNoteService locationNoteService;

    public LocationNoteController(LocationService locationService, LocationNoteService locationNoteService) {
        this.locationService = locationService;
        this.locationNoteService = locationNoteService;
    }

    @GetMapping("/{id}/add-note")
    public String showAddNoteForm(@PathVariable Long id, Model model) {
        LocationDTO location = locationService.getLocationById(id).orElseThrow(() ->
                new RuntimeException("Location not found"));
        model.addAttribute("location", location);
        model.addAttribute("noteRequest", new LocationNoteRequestDTO());
        return "add-note";
    }

    @PostMapping("/{id}/add-note")
    public String submitNote(@PathVariable Long id,
                             @ModelAttribute LocationNoteRequestDTO noteRequest,
                             Authentication authentication) {

        String username = authentication.getName();
        locationNoteService.addNote(id, username, noteRequest);

        return "redirect:/map";
    }
}