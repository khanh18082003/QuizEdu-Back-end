package com.tkt.quizedu.utils;

public class GenerateVerificationCode {
  private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
  private static final int CODE_LENGTH = 6;

  public static String generateCode() {
    StringBuilder code = new StringBuilder(CODE_LENGTH);
    for (int i = 0; i < CODE_LENGTH; i++) {
      int index = (int) (Math.random() * CHARACTERS.length());
      code.append(CHARACTERS.charAt(index));
    }
    return code.toString();
  }
}
