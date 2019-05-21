package com.georent.dto;

import lombok.Data;

@Data
public class GeoRentUserInfoDto {

    private long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
}
