package com.georent.domain;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
    public class Lot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

    @ManyToOne
        private  GeoRentUser userId;


    @OneToOne(mappedBy = "id")
        private  Description description;

//    @OneToOne
//    private Coordinates coordinates;
}
