package com.georent.domain;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * This class is for showcase purposes only
 * {@link Data} annotation generates getters, setters, toString() and other methods.
 */
@Data
@Entity
public class TestObject {
    @Id
    private Long id;
    private String name;
    private String secretString;
    private Long secretLong;
}
