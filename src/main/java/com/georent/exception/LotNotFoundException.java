package com.georent.exception;

public class LotNotFoundException extends RuntimeException {
    public LotNotFoundException() {
    }

    public LotNotFoundException(final String message) {
        super(message);
    }

    public LotNotFoundException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
