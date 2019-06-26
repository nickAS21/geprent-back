package com.georent.exception;

import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

public class ValidMultiPartFileException extends RuntimeException {


    /**
     * Constructs a <code>ValidMultiPartFileException</code>.
     */
    public ValidMultiPartFileException() {
    }

    /**
     * Constructs a <code>ValidMultiPartFileException</code> with the specified message.
     *
     * @param message the detail message.
     */
    public ValidMultiPartFileException(final String message) {
        super(message);
    }
}
