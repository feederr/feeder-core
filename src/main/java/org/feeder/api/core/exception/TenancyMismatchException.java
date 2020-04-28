package org.feeder.api.core.exception;

public class TenancyMismatchException extends RuntimeException {

  public TenancyMismatchException() {
    super();
  }

  public TenancyMismatchException(String message) {
    super(message);
  }

  public TenancyMismatchException(String message, Throwable cause) {
    super(message, cause);
  }

  public TenancyMismatchException(Throwable cause) {
    super(cause);
  }

  protected TenancyMismatchException(String message, Throwable cause, boolean enableSuppression,
      boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
