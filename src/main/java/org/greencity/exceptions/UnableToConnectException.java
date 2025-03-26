package org.greencity.exceptions;

public class UnableToConnectException extends RuntimeException {
    public UnableToConnectException(String message) {
        super(message);
    }

    public UnableToConnectException(Throwable cause) {
        super(cause);
    }
}
