package com.georent.domain;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
public class Coordinates {

    @Id
    private Long id;

    private Float longitude;

    private Float latitude;

    private String address;

    //    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private Lot lot;
}
