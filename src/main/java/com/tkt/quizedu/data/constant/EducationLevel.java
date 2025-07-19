package com.tkt.quizedu.data.constant;

public enum EducationLevel {
  PRIMARY("Primary"),
  SECONDARY("Secondary"),
  HIGH_SCHOOL("High School"),
  UNDERGRADUATE("Undergraduate"),
  POSTGRADUATE("Postgraduate"),
  DOCTORATE("Doctorate");

  private final String level;

  EducationLevel(String level) {
    this.level = level;
  }

  public String getLevel() {
    return level;
  }
}
