package com.tkt.quizedu.data.validator;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import com.tkt.quizedu.data.constant.EducationLevel;

@Documented
@Target({ElementType.FIELD})
@Retention(RUNTIME)
@Constraint(validatedBy = LevelValidator.class)
public @interface LevelValid {

  EducationLevel[] anyOf() default {
    EducationLevel.PRIMARY,
    EducationLevel.SECONDARY,
    EducationLevel.HIGH_SCHOOL,
    EducationLevel.UNDERGRADUATE,
    EducationLevel.POSTGRADUATE,
    EducationLevel.DOCTORATE
  };

  String message() default "MESSAGE_INVALID_ARGUMENT";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
