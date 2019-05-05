package com.georent.dto;

import lombok.Data;

@Data
public class RegistrationRequestDTO {
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private long phoneNumber;
}
