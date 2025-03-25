package org.greencity.exceptions;

public class UnexpectedResponseException extends RuntimeException {
    public UnexpectedResponseException(String message) {
        super(message);
    }

  public UnexpectedResponseException(Throwable cause) {
    super(cause);
  }
}
