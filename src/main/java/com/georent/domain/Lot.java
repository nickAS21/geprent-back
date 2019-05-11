package com.georent.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

@Getter
@Setter
@Entity
public class Lot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    private GeoRentUser geoRentUser;

    @OneToOne(
            mappedBy = "lot",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY,
            optional = false)
    private Description description;

    @OneToOne(
            mappedBy = "lot",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY,
            optional = false)
    private Coordinates coordinates;

    public void setDescription(Description description) {
        if (description == null) {
            if (this.description != null) {
                this.description.setLot(null);
            }
        }
        else {
            description.setLot(this);
        }
        this.description = description;
    }

    public void setCoordinates(Coordinates coordinates) {
        if (coordinates == null) {
            if (this.coordinates != null) {
                this.coordinates.setLot(null);
            }
        }
        else {
            coordinates.setLot(this);
        }
        this.coordinates = coordinates;
    }

//    public Long getLotId() {
//        return lotId;
//    }
//
//    public void setLotId(final Long lotId) {
//        this.lotId = lotId;
//    }
//
//    public GeoRentUser getGeoRentUser() {
//        return geoRentUser;
//    }
//
//    public void setGeoRentUser(final GeoRentUser geoRentUser) {
//        this.geoRentUser = geoRentUser;
//    }
}
