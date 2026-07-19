package com.mikelekan.scenefinder.controller;

import com.mikelekan.scenefinder.dto.LocationDTO;
import com.mikelekan.scenefinder.service.LocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

@Controller
@RequiredArgsConstructor
public class LocationPageController
{
    private final LocationService locationService;

    @GetMapping("/locations/add")
    public String showAddLocationForm(Model model)
    {
        model.addAttribute("locationRequest", new LocationDTO());
        return "add-location";
    }

    @PostMapping("/locations/add")
    public String submitLocation(@ModelAttribute LocationDTO locationRequest)
    {
        locationService.addNewLocation(locationRequest);
        return "redirect:/map";
    }
}