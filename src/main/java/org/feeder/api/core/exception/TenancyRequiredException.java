package org.feeder.api.core.exception;

public class TenancyRequiredException extends RuntimeException {

  public TenancyRequiredException() {
    super();
  }

  public TenancyRequiredException(String message) {
    super(message);
  }

  public TenancyRequiredException(String message, Throwable cause) {
    super(message, cause);
  }

  public TenancyRequiredException(Throwable cause) {
    super(cause);
  }

  protected TenancyRequiredException(String message, Throwable cause, boolean enableSuppression,
      boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
