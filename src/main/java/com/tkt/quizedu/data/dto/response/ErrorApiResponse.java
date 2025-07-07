package com.tkt.quizedu.data.dto.response;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorApiResponse extends ApiResponse {
  LocalDateTime timeStamp;
  String path;
  String error;

  public ErrorApiResponse(
      String code, int status, String message, LocalDateTime timeStamp, String path, String error) {
    super(code, status, message);
    this.timeStamp = timeStamp;
    this.path = path;
    this.error = error;
  }

  public static ErrorApiResponseBuilder builder() {
    return new ErrorApiResponseBuilder();
  }

  public static class ErrorApiResponseBuilder {
    private String code;
    private int status;
    private String message;
    private LocalDateTime timeStamp;
    private String path;
    private String error;

    public ErrorApiResponseBuilder code(String code) {
      this.code = code;
      return this;
    }

    public ErrorApiResponseBuilder status(int status) {
      this.status = status;
      return this;
    }

    public ErrorApiResponseBuilder message(String message) {
      this.message = message;
      return this;
    }

    public ErrorApiResponseBuilder timeStamp(LocalDateTime timeStamp) {
      this.timeStamp = timeStamp;
      return this;
    }

    public ErrorApiResponseBuilder path(String path) {
      this.path = path;
      return this;
    }

    public ErrorApiResponseBuilder error(String error) {
      this.error = error;
      return this;
    }

    public ErrorApiResponse build() {
      return new ErrorApiResponse(
          this.code, this.status, this.message, this.timeStamp, this.path, this.error);
    }
  }
}
