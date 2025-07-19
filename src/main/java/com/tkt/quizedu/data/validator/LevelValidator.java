package com.tkt.quizedu.data.validator;

import java.util.Arrays;
import java.util.List;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import com.tkt.quizedu.data.constant.EducationLevel;
import com.tkt.quizedu.data.constant.UserRole;

public class LevelValidator implements ConstraintValidator<LevelValid, String> {
  private List<EducationLevel> validLevels;

  @Override
  public void initialize(LevelValid constraintAnnotation) {
    validLevels = Arrays.asList(constraintAnnotation.anyOf());
  }

  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {
    if (value == null || value.isBlank()) {
      return false;
    }
    try {
      EducationLevel educationLevel = EducationLevel.valueOf(value.toUpperCase());
      return validLevels.contains(educationLevel);
    } catch (IllegalArgumentException e) {
      return false;
    }
  }
}
