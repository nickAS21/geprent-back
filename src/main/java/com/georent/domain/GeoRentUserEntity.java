package com.georent.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class GeoRentUserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private long phoneNumber;

    public GeoRentUserEntity(){}

    public GeoRentUserEntity(String firstName, String lastName, String email, String password, long phoneNumber) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.phoneNumber = phoneNumber;
    }

    @Override
    public String toString() {
        return String.format(
                "GeoRentUserEntity[id=%d, firstName=%s, lastName=%s, email=%s, password=%s, phoneNumber=%s]",
                id,firstName,lastName,email,password,phoneNumber);
    }
}
