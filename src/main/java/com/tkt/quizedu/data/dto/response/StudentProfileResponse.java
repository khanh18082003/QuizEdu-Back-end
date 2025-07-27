package com.tkt.quizedu.data.dto.response;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.tkt.quizedu.data.constant.EducationLevel;
import com.tkt.quizedu.data.constant.UserRole;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StudentProfileResponse extends UserBaseResponse implements Serializable {

  EducationLevel level;
  String schoolName;

  public StudentProfileResponse(
      String id,
      String email,
      String firstName,
      String lastName,
      String displayName,
      String avatar,
      boolean noPassword,
      boolean isActive,
      UserRole role,
      LocalDateTime createdAt,
      LocalDateTime updatedAt,
      EducationLevel level,
      String schoolName) {
    super(
        id,
        email,
        firstName,
        lastName,
        displayName,
        avatar,
        noPassword,
        isActive,
        role,
        createdAt,
        updatedAt);
    this.level = level;
    this.schoolName = schoolName;
  }
}
