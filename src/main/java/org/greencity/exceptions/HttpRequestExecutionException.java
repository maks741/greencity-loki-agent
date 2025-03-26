package org.greencity.exceptions;

public class HttpRequestExecutionException extends RuntimeException {
    public HttpRequestExecutionException(String message) {
        super(message);
    }

    public HttpRequestExecutionException(Throwable cause) {
        super(cause);
    }
}
