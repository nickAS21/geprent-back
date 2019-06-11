package com.georent.domain;

import lombok.Data;
import org.joda.time.DateTime;

import javax.persistence.*;

@Data
@Entity
public class RentOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private long lotId;
    private long renteeId;
    private DateTime startDateTime;
    private DateTime endDateTime;
    private RentOrderStatus status;
}
