package com.tkt.quizedu.data.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SuccessApiResponse<T> extends ApiResponse {
  T data;

  public SuccessApiResponse(String code, int status, String message, T data) {
    super(code, status, message);
    this.data = data;
  }

  public static <T> SuccessApiResponseBuilder<T> builder() {
    return new SuccessApiResponseBuilder<>();
  }

  public static class SuccessApiResponseBuilder<T> {
    private String code;
    private int status;
    private String message;
    private T data;

    public SuccessApiResponseBuilder<T> code(String code) {
      this.code = code;
      return this;
    }

    public SuccessApiResponseBuilder<T> status(int status) {
      this.status = status;
      return this;
    }

    public SuccessApiResponseBuilder<T> message(String message) {
      this.message = message;
      return this;
    }

    public SuccessApiResponseBuilder<T> data(T data) {
      this.data = data;
      return this;
    }

    public SuccessApiResponse<T> build() {
      return new SuccessApiResponse<>(this.code, this.status, this.message, this.data);
    }
  }
}
