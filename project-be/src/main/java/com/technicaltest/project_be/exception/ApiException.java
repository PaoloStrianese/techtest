package com.technicaltest.project_be.exception;

import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@Getter
public class ApiException extends RuntimeException {
  private final int status;
  private final Map<String, String> errors;
  private final String errorCode;

  @Builder
  public ApiException(String message, int status, Map<String, String> errors,
                      Throwable cause, String errorCode) {
    super(message, cause);
    this.status = status == 0 ? 400 : status;
    this.errors = errors != null ? errors : Map.of();
    this.errorCode = errorCode;
  }

  // Metodi factory helper per errori comuni
  public static ApiException notFound(String resource, String id) {
    return ApiException.builder()
        .message(resource + " not found with id: " + id)
        .status(404)
        .errorCode("NOT_FOUND")
        .build();
  }

  public static ApiException internalError(String operation, Throwable cause) {
    return ApiException.builder()
        .message(operation + " failed due to internal error")
        .status(500)
        .errorCode("INTERNAL_ERROR")
        .cause(cause)
        .build();
  }

  public static ApiException sendMessageError(String message) {
    return ApiException.builder()
        .message(message)
        .status(502)
        .errorCode("MESSAGE_BROKER_ERROR")
        .build();
  }

  public static ApiException alreadyDeleted(String resource, String id) {
    return ApiException.builder()
        .message(resource + " with id " + id + " is already deleted")
        .status(410)
        .errorCode("ALREADY_DELETED")
        .errors(Map.of(
            "resource", resource,
            "id", id,
            "reason", "Resource has been previously deleted"
        ))
        .build();
  }
}