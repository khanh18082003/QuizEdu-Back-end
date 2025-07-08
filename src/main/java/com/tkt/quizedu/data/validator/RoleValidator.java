package com.tkt.quizedu.data.validator;

import java.util.Arrays;
import java.util.List;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import com.tkt.quizedu.data.constant.UserRole;

public class RoleValidator implements ConstraintValidator<RoleValid, String> {
  private List<UserRole> validRoles;

  @Override
  public void initialize(RoleValid constraintAnnotation) {
    validRoles = Arrays.asList(constraintAnnotation.anyOf());
  }

  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {
    if (value == null || value.isBlank()) {
      return false;
    }
    try {
      UserRole role = UserRole.valueOf(value.toUpperCase());
      return validRoles.contains(role);
    } catch (IllegalArgumentException e) {
      return false;
    }
  }
}
