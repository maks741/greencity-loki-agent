package org.greencity.exceptions;

public class InvalidProtocolException extends RuntimeException {
    public InvalidProtocolException(String message) {
        super(message);
    }

  public InvalidProtocolException(Throwable cause) {
    super(cause);
  }
}
