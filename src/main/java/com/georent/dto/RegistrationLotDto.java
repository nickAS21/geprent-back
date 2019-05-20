package com.georent.dto;

import lombok.Data;

@Data
public class RegistrationLotDto {

    //    Coordinates
    private Long price;
    private Float longitude;
    private Float latitude;
    private String address;

    //         Description
    private String itemPath;
    private String itemName;
    private String lotDescription;

}
