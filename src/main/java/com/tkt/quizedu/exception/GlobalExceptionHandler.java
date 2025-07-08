package com.tkt.quizedu.exception;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import jakarta.validation.ConstraintViolation;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import com.tkt.quizedu.component.Translator;
import com.tkt.quizedu.data.constant.ErrorCode;
import com.tkt.quizedu.data.dto.response.ErrorApiResponse;

import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j(topic = "GLOBAL-EXCEPTION")
public class GlobalExceptionHandler {

  private static final String MIN_CONST = "min";
  private static final String MAX_CONST = "max";

  @ExceptionHandler(QuizException.class)
  ResponseEntity<ErrorApiResponse> handleAppException(
      QuizException appException, WebRequest request) {
    log.error("Exception occurred: {}", appException.getMessage(), appException);
    ErrorCode errorCode = appException.getErrorCode();
    return ResponseEntity.status(errorCode.getStatusCode())
        .body(
            ErrorApiResponse.builder()
                .timeStamp(LocalDateTime.now())
                .code(errorCode.getCode())
                .status(errorCode.getStatusCode().value())
                .error(errorCode.getStatusCode().getReasonPhrase())
                .path(request.getDescription(false).replace("uri=", ""))
                .message(appException.getMessage())
                .build());
  }

  @ExceptionHandler(RuntimeException.class)
  ResponseEntity<ErrorApiResponse> handleRunTimeException(
      RuntimeException runtimeException, WebRequest request) {

    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(
            ErrorApiResponse.builder()
                .timeStamp(LocalDateTime.now())
                .code(ErrorCode.MESSAGE_UN_CATEGORIES.getCode())
                .status(HttpStatus.BAD_REQUEST.value())
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .path(request.getDescription(false).replace("uri=", ""))
                .message(runtimeException.getMessage())
                .build());
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  ResponseEntity<List<ErrorApiResponse>> handleValidationException(
      MethodArgumentNotValidException exception, WebRequest request) {
    var errorApiResponses =
        exception.getFieldErrors().stream()
            .map(
                errorField -> {
                  var constraint =
                      exception
                          .getBindingResult()
                          .getAllErrors()
                          .get(exception.getFieldErrors().indexOf(errorField))
                          .unwrap(ConstraintViolation.class);
                  Map<String, Object> attributes =
                      constraint.getConstraintDescriptor().getAttributes();
                  return ErrorApiResponse.builder()
                      .timeStamp(LocalDateTime.now())
                      .code(ErrorCode.valueOf(errorField.getDefaultMessage()).getCode())
                      .status(
                          ErrorCode.valueOf(errorField.getDefaultMessage()).getStatusCode().value())
                      .error(
                          ErrorCode.valueOf(errorField.getDefaultMessage())
                              .getStatusCode()
                              .getReasonPhrase())
                      .path(request.getDescription(false).replace("uri=", ""))
                      .message(
                          mapMessage(
                              Translator.toLocale(
                                  ErrorCode.valueOf(errorField.getDefaultMessage()).getCode()),
                              attributes,
                              errorField.getField()))
                      .build();
                })
            .toList();
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorApiResponses);
  }

  private String mapMessage(String message, Map<String, Object> attributes, String fieldError) {
    if (attributes.containsKey(MIN_CONST)) {
      String minValue = String.valueOf(attributes.get(MIN_CONST));
      message = message.replace("{" + MIN_CONST + "}", minValue);
    }
    if (attributes.containsKey(MAX_CONST)) {
      String maxValue = String.valueOf(attributes.get(MAX_CONST));
      message = message.replace("{" + MAX_CONST + "}", maxValue);
    }
    if (attributes.containsKey("anyOf")) {
      Object anyOfObj = attributes.get("anyOf");
      String anyOfValue;
      if (anyOfObj instanceof Object[] array) {
        anyOfValue = Arrays.stream(array).map(Object::toString).collect(Collectors.joining(", "));
      } else {
        anyOfValue = String.valueOf(anyOfObj);
      }
      message = message.replace("{anyOf}", anyOfValue);
    }
    return String.format(message, fieldError);
  }
}
