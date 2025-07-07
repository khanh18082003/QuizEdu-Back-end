package com.tkt.quizedu.exception;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import com.tkt.quizedu.data.constant.ErrorCode;
import com.tkt.quizedu.data.dto.response.ErrorApiResponse;

import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j(topic = "GLOBAL-EXCEPTION")
public class GlobalExceptionHandler {

  @ExceptionHandler(QuizException.class)
  ResponseEntity<ErrorApiResponse> handleAppException(
      QuizException appException, WebRequest request) {
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
}
