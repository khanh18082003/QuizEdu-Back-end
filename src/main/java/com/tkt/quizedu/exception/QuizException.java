package com.tkt.quizedu.exception;

import com.tkt.quizedu.component.Translator;
import com.tkt.quizedu.data.constant.ErrorCode;

import lombok.Getter;

@Getter
public class QuizException extends RuntimeException {

  private final ErrorCode errorCode;

  public QuizException(ErrorCode errorCode) {
    super(Translator.toLocale(errorCode.getCode()));
    this.errorCode = errorCode;
  }
}
