package com.georent.domain;

import lombok.Data;

/**
 * This class is for showcase purposes only
 * {@link Data} annotation generates getters, setters, toString() and other methods.
 */
@Data
public class TestObject {
    private Long id;
    private String name;
    private String secretString;
    private Long secretLong;
}
