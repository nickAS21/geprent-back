package com.georent.dto;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class LoginRequestDTO {

    @Email()
    @NotBlank()
    @Size(max = 40)
    private String email;

    @NotBlank()
    @Size(min = 6, max = 100)
    private String password;
}
