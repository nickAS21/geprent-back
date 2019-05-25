package com.georent.exception;

public class UserRegistrationException extends RuntimeException {

    /**
     * Constructs a <code>LotNotFoundException</code>.
     */
    public UserRegistrationException() {
    }

    /**
     * Constructs a <code>LotNotFoundException</code> with the specified message.
     *
     * @param message the detail message.
     */
    public UserRegistrationException(final String message) {
        super(message);
    }

    /**
     * Constructs a {@code UsernameNotFoundException} with the specified message and root
     * cause.
     *
     * @param message the detail message.
     * @param cause root cause
     */
    public UserRegistrationException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
