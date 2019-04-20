package com.georent.dto;


import lombok.Data;

/**
 * Data transfer object to carry Address info to client.
 */
@Data
public class AddressDTO {

    private Long id;

    private Long userId;

    private CoordinatesDTO coordinatesDTO;
}
