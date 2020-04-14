package org.feeder.api.core.exception;

import static org.springframework.core.Ordered.HIGHEST_PRECEDENCE;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.METHOD_NOT_ALLOWED;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNSUPPORTED_MEDIA_TYPE;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import org.feeder.api.core.domain.ApiError;
import org.springframework.beans.TypeMismatchException;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

@RestControllerAdvice
@Order(HIGHEST_PRECEDENCE)
public class GlobalExceptionHandler {

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiError> handle(Exception exception) {

    ApiError error = new ApiError(
        INTERNAL_SERVER_ERROR,
        exception.getMessage(),
        "Something went wrong"
    );

    return buildResponseEntity(error);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ApiError> handle(MethodArgumentNotValidException exception) {

    ApiError error = ApiError.builder()
        .status(BAD_REQUEST)
        .message(exception.getMessage())
        .errors(buildErrorsFromBindingResult(exception.getBindingResult()))
        .build();

    return buildResponseEntity(error);
  }

  @ExceptionHandler(BindException.class)
  public ResponseEntity<ApiError> handle(BindException exception) {

    ApiError error = ApiError.builder()
        .status(BAD_REQUEST)
        .message(exception.getMessage())
        .errors(buildErrorsFromBindingResult(exception.getBindingResult()))
        .build();

    return buildResponseEntity(error);
  }

  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<ApiError> handle(ConstraintViolationException exception) {

    List<String> errors = buildErrorsFromConstraintViolations(exception.getConstraintViolations());

    ApiError error = ApiError.builder()
        .status(BAD_REQUEST)
        .message(exception.getMessage())
        .errors(errors)
        .build();

    return buildResponseEntity(error);
  }

  @ExceptionHandler(DataIntegrityViolationException.class)
  public ResponseEntity<ApiError> handle(DataIntegrityViolationException exception) {

    List<String> errors = new ArrayList<>();

    if (exception.getCause() instanceof ConstraintViolationException) {
      errors.addAll(
          buildErrorsFromConstraintViolations(
              ((ConstraintViolationException) exception.getCause()).getConstraintViolations()
          )
      );
    } else {
      errors.add("error occurred");
    }

    ApiError error = ApiError.builder()
        .status(BAD_REQUEST)
        .message(exception.getMessage())
        .errors(errors)
        .build();

    return buildResponseEntity(error);
  }

  @ExceptionHandler({MethodArgumentTypeMismatchException.class, TypeMismatchException.class})
  public ResponseEntity<ApiError> handle(TypeMismatchException exception) {

    ApiError error = new ApiError(
        BAD_REQUEST,
        exception.getMessage(),
        String.format("%s required type: %s for value %s",
            exception.getPropertyName(),
            exception.getRequiredType(),
            exception.getValue())
    );

    return buildResponseEntity(error);
  }

  @ExceptionHandler(NoHandlerFoundException.class)
  public ResponseEntity<ApiError> handle(NoHandlerFoundException exception) {

    ApiError error = new ApiError(
        NOT_FOUND,
        exception.getMessage(),
        String.format("Handler for %s %s not found",
            exception.getHttpMethod(),
            exception.getRequestURL())
    );

    return buildResponseEntity(error);
  }

  @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
  public ResponseEntity<ApiError> handle(HttpRequestMethodNotSupportedException exception) {

    ApiError error = new ApiError(
        METHOD_NOT_ALLOWED,
        exception.getMessage(),
        String.format("%s method is not supported for this request. Supported methods: %s",
            exception.getMethod(),
            Arrays.toString(exception.getSupportedMethods()))
    );

    return buildResponseEntity(error);
  }

  @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
  public ResponseEntity<ApiError> handle(HttpMediaTypeNotSupportedException exception) {

    ApiError error = new ApiError(
        UNSUPPORTED_MEDIA_TYPE,
        exception.getMessage(),
        String.format("%s media type is not supported. Supported media types: %s",
            exception.getContentType(),
            exception.getSupportedMediaTypes())
    );

    return buildResponseEntity(error);
  }

  @ExceptionHandler(HttpMessageConversionException.class)
  public ResponseEntity<ApiError> handle(HttpMessageConversionException exception) {

    List<String> errors = buildErrorsFromHttpMessageConversionException(exception);

    ApiError error = ApiError.builder()
        .status(BAD_REQUEST)
        .message(exception.getMessage())
        .errors(errors)
        .build();

    return buildResponseEntity(error);
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<ApiError> handle(HttpMessageNotReadableException exception) {

    List<String> errors = buildErrorsFromHttpMessageConversionException(exception);

    ApiError error = ApiError.builder()
        .status(BAD_REQUEST)
        .message(exception.getMessage())
        .errors(errors)
        .build();

    return buildResponseEntity(error);
  }

  @ExceptionHandler(EntityNotFoundException.class)
  public ResponseEntity<ApiError> handle(EntityNotFoundException exception) {

    ApiError error = new ApiError(
        NOT_FOUND,
        exception.getMessage(),
        String.format("%s = %s not found",
            exception.getEntityClass().getSimpleName(),
            exception.getEntityId())
    );

    return buildResponseEntity(error);
  }

  private List<String> buildErrorsFromBindingResult(BindingResult bindingResult) {

    List<String> errors = new ArrayList<>();

    errors.addAll(fieldErrors(bindingResult));
    errors.addAll(globalErrors(bindingResult));

    return errors;
  }

  private List<String> fieldErrors(BindingResult bindingResult) {
    return bindingResult.getFieldErrors()
        .stream()
        .map(error -> error.getField() + ": " + error.getDefaultMessage())
        .collect(Collectors.toList());
  }

  private List<String> globalErrors(BindingResult bindingResult) {
    return bindingResult.getGlobalErrors()
        .stream()
        .map(error -> error.getObjectName() + ": " + error.getDefaultMessage())
        .collect(Collectors.toList());
  }

  private List<String> buildErrorsFromConstraintViolations(
      Set<ConstraintViolation<?>> violations) {
    return violations.stream()
        .map(violation ->
            String.format("%s %s: %s",
                violation.getRootBeanClass().getName(),
                violation.getPropertyPath(),
                violation.getMessage())
        )
        .collect(Collectors.toList());
  }

  private List<String> buildErrorsFromHttpMessageConversionException(
      HttpMessageConversionException exception) {

    List<String> errors = new ArrayList<>();
    Throwable cause = exception.getMostSpecificCause();

    if (cause instanceof InvalidFormatException) {

      InvalidFormatException formatException = (InvalidFormatException) cause;

      String field = formatException.getPath().stream()
          .map(JsonMappingException.Reference::getFieldName)
          .collect(Collectors.joining("."));

      errors.add(field + " invalid");

    } else if (cause instanceof DateTimeParseException) {

      errors.add("DateTime invalid");

    } else if (cause instanceof IllegalArgumentException) {

      errors.add("Argument invalid");

    } else {

      errors.add("Invalid format");

    }

    return errors;
  }

  private ResponseEntity<ApiError> buildResponseEntity(ApiError error) {
    return ResponseEntity.status(error.getStatus())
        .contentType(MediaType.APPLICATION_JSON)
        .body(error);
  }
}
