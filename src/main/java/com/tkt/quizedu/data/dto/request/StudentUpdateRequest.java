package com.tkt.quizedu.data.dto.request;

import java.io.Serializable;

import lombok.*;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StudentUpdateRequest extends UserUpdateDTORequest implements Serializable {
  String level;
  String schoolName;

  public StudentUpdateRequest(
      String firstName, String lastName, String displayName, String level, String schoolName) {
    super(firstName, lastName, displayName);
    this.level = level;
    this.schoolName = schoolName;
  }
}
