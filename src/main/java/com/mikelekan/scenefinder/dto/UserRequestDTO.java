package com.mikelekan.scenefinder.dto;

import lombok.*;

@Data
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserRequestDTO
{
    private Long id;
    private String userName;
    private String password;
    private String email;
}
