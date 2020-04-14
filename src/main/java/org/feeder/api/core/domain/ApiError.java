package org.feeder.api.core.domain;

import java.util.List;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Data
@Builder
@RequiredArgsConstructor
public class ApiError {

  private final HttpStatus status;

  private final String message;

  private final List<String> errors;

  public ApiError(HttpStatus status, String message, String error) {
    this.status = status;
    this.message = message;
    this.errors = List.of(error);
  }
}
