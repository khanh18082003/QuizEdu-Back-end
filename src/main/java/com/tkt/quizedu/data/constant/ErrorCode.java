package com.tkt.quizedu.data.constant;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public enum ErrorCode {
  MESSAGE_SUCCESS("M000", HttpStatus.OK),
  MESSAGE_UN_CATEGORIES("M999", HttpStatus.BAD_REQUEST),
  // Validation Errors
  MESSAGE_NOT_BLANK("M001", HttpStatus.BAD_REQUEST),
  MESSAGE_INVALID_EMAIL("M002", HttpStatus.BAD_REQUEST),
  MESSAGE_PASSWORD_SIZE("M003", HttpStatus.BAD_REQUEST),
  MESSAGE_INVALID_FORMAT_PASSWORD("M005", HttpStatus.BAD_REQUEST),
  MESSAGE_INVALID_ARGUMENT("M004", HttpStatus.BAD_REQUEST),

  // Authentication Errors and Authorization Errors
  MESSAGE_UNAUTHENTICATED("M100", HttpStatus.UNAUTHORIZED),
  MESSAGE_UNAUTHORIZED("M101", HttpStatus.FORBIDDEN),
  MESSAGE_INVALID_ID("M102", HttpStatus.NOT_FOUND);

  private final String code;
  private final HttpStatus statusCode;

  ErrorCode(String code, HttpStatus statusCode) {
    this.code = code;
    this.statusCode = statusCode;
  }
}
