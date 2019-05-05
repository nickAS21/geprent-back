package com.georent.exception;

public class UserLoginException extends RuntimeException {
    public UserLoginException() {
    }

    public UserLoginException(final String message) {
        super(message);
    }

    public UserLoginException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
