package com.tkt.quizedu.data.dto.request;

import java.util.Set;

import lombok.*;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TeacherUpdateRequest extends UserUpdateRequest {
  Set<String> subjects;
  String experience;
  String schoolName;

  public TeacherUpdateRequest(
      String firstName,
      String lastName,
      String displayName,
      Set<String> subjects,
      String experience,
      String schoolName) {
    super(firstName, lastName, displayName);
    this.subjects = subjects;
    this.experience = experience;
    this.schoolName = schoolName;
  }
}
