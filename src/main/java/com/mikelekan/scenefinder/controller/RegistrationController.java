package com.mikelekan.scenefinder.controller;

import com.mikelekan.scenefinder.dto.UserRequestDTO;
import com.mikelekan.scenefinder.dto.UserResponseDTO;
import com.mikelekan.scenefinder.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/registration")
@CrossOrigin(origins = "*")
public class RegistrationController
{
    private final UserService userService;

    public RegistrationController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<UserResponseDTO> addNewUser(@RequestBody UserRequestDTO newUser)
    {
        UserResponseDTO createdUserResponseDTO = userService.addNewUser(newUser);

        return ResponseEntity.status(HttpStatus.CREATED).body(createdUserResponseDTO);
    }
}
