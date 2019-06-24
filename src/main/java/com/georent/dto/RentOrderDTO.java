package com.georent.dto;

import com.georent.domain.RentOrderStatus;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.time.LocalDateTime;

@Data
public class RentOrderDTO {
    private Long orderId;
    private LotDTO lotDTO;
    private Long renteeId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String status;
}