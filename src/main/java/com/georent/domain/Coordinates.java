package com.georent.domain;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.MapsId;
import javax.persistence.OneToOne;

@Data
@Entity
public class Coordinates {

    @Id
    private Long id;

    private Float longitude;

    private Float latitude;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    private Lot lot;
}
