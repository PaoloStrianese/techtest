package com.technicaltest.project_be.handler;

import com.technicaltest.project_be.exception.ApiException;
import com.technicaltest.project_be.response.internal.HttpErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ControllerAdvice
@Slf4j
public class ExceptionHandler extends ResponseEntityExceptionHandler {

  @Override
  protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                HttpHeaders headers, HttpStatusCode status, WebRequest request) {
    Map<String, String> errors = new HashMap<>();
    List<String> generalErrors = new ArrayList<>();
    ex.getBindingResult().getAllErrors().forEach((error) -> {
      if (error instanceof FieldError fieldErr) {
        String fieldName = fieldErr.getField();
        String errorMessage = fieldErr.getDefaultMessage();
        errors.put(fieldName, errorMessage);
      } else {
        generalErrors.add(error.getDefaultMessage());
      }
    });

    HttpErrorResponse response = HttpErrorResponse.of("Unprocessable entity", 422, errors, generalErrors);

    return new ResponseEntity<>(response, HttpStatus.UNPROCESSABLE_ENTITY);
  }

  @org.springframework.web.bind.annotation.ExceptionHandler(ApiException.class)
  public ResponseEntity<HttpErrorResponse> handleException(ApiException e) {
    log.error("Handling ApiException", e);

    Map<String, String> errors = e.getErrors() != null && !e.getErrors().isEmpty()
        ? e.getErrors()
        : Map.of("location", e.getCause() != null ? e.getCause().toString() : "Unknown cause");

    var response = HttpErrorResponse.of(
        e.getMessage(),
        e.getStatus(),
        errors,
        null
    );

    return new ResponseEntity<>(response, HttpStatus.valueOf(e.getStatus()));
  }

  //Gestione eccezioni non configurate
  @org.springframework.web.bind.annotation.ExceptionHandler(Exception.class)
  public ResponseEntity<HttpErrorResponse> handleException(Exception e) {
    log.error("Unhandled exception", e);

    String location = e.getStackTrace().length > 0
        ? e.getStackTrace()[0].toString()
        : "No stack trace available";

    var response = HttpErrorResponse.of(
        "Unexpected error: " + e.getMessage(),
        500,
        Map.of("location", location), // map con location
        null
    );
    return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
  }


}