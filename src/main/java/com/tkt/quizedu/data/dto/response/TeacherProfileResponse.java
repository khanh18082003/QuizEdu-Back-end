package com.tkt.quizedu.data.dto.response;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.tkt.quizedu.data.constant.UserRole;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TeacherProfileResponse extends UserBaseResponse implements Serializable {
  Set<String> subjects;
  String experience;
  String schoolName;

  public TeacherProfileResponse(
      String id,
      String email,
      String firstName,
      String lastName,
      String displayName,
      String avatar,
      boolean isActive,
      UserRole role,
      LocalDateTime createdAt,
      LocalDateTime updatedAt,
      Set<String> subjects,
      String experience,
      String schoolName) {
    super(
        id, email, firstName, lastName, displayName, avatar, isActive, role, createdAt, updatedAt);
    this.subjects = subjects;
    this.experience = experience;
    this.schoolName = schoolName;
  }
}
