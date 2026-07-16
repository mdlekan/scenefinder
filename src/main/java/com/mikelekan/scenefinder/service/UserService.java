package com.mikelekan.scenefinder.service;

import com.mikelekan.scenefinder.dto.UserRequestDTO;
import com.mikelekan.scenefinder.dto.UserResponseDTO;
import com.mikelekan.scenefinder.model.User;
import com.mikelekan.scenefinder.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService
{
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder)
    {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserResponseDTO addNewUser(UserRequestDTO newUserRequestDTO)
    {
        User newUser = createUser(newUserRequestDTO);

        User savedUser = userRepository.save(newUser);

        return createUserDTO(savedUser);
    }

    private UserResponseDTO createUserDTO(User newUser)
    {
        return UserResponseDTO.builder()
                .id(newUser.getId())
                .email(newUser.getEmail())
                .userName(newUser.getUsername())
                .build();
    }

    private User createUser(UserRequestDTO newUserRequestDTO)
    {
        return User.builder()
                .username(newUserRequestDTO.getUserName())
                .email(newUserRequestDTO.getEmail())
                .passwordHash(passwordEncoder.encode(newUserRequestDTO.getPassword()))
                .build();
    }
}
