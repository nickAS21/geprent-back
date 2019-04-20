package com.georent.domain;

import lombok.Data;

/**
 * The address entity which is used to provide addresses of lots.
 */

@Data
public class Address {

    private Long id;

    private GeorentUser user;

    private Coordinates coordinates;
}
