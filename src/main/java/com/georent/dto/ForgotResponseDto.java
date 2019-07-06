package com.georent.dto;

import com.georent.domain.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
public class ForgotResponseDto {
    private int statusCode;
    private LocalDate dateSent;
    private Set<UserRole> role;
}

