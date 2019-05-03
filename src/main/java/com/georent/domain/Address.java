package com.georent.domain;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * The address entity which is used to provide addresses of lots.
 */

@Data
@Entity
public class Address {

    @Id
    private Long id;

    private Long userId;

    private Long coordId;
}
