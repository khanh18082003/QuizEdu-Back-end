package com.tkt.quizedu.data.validator;

import java.util.List;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import com.tkt.quizedu.data.constant.UserRole;

public class RoleValidator implements ConstraintValidator<RoleValid, String> {
  private List<UserRole> validRoles;

  @Override
  public void initialize(RoleValid constraintAnnotation) {
    validRoles = List.of(constraintAnnotation.anyOf());
  }

  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {
    try {
      UserRole role = UserRole.valueOf(value.toUpperCase());
      return validRoles.contains(role);
    } catch (IllegalArgumentException e) {
      return false;
    }
  }
}
