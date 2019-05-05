package com.georent.exception;

public class UserRegistrationException extends RuntimeException {
    public UserRegistrationException() {
    }

    public UserRegistrationException(final String message) {
        super(message);
    }

    public UserRegistrationException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
