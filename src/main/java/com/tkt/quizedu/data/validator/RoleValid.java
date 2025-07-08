package com.tkt.quizedu.data.validator;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import com.tkt.quizedu.data.constant.UserRole;

@Documented
@Target({ElementType.FIELD})
@Retention(RUNTIME)
@Constraint(validatedBy = RoleValidator.class)
public @interface RoleValid {
  UserRole[] anyOf() default {UserRole.ADMIN, UserRole.STUDENT, UserRole.TEACHER};

  String message() default "MESSAGE_INVALID_ARGUMENT";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
