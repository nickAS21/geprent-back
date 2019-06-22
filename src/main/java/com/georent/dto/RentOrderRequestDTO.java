package com.georent.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RentOrderRequestDTO {
    private Long lotId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}
