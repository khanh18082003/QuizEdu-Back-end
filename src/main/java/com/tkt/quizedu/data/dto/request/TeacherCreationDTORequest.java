package com.tkt.quizedu.data.dto.request;

import java.io.Serializable;
import java.util.Set;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import lombok.*;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TeacherCreationDTORequest extends UserCreationDTORequest implements Serializable {

  @NotEmpty(message = "MESSAGE_NOT_EMPTY")
  Set<String> subjects;

  @NotBlank(message = "MESSAGE_NOT_BLANK")
  String experience;

  @NotBlank(message = "MESSAGE_NOT_BLANK")
  String schoolName;

  public TeacherCreationDTORequest(
      String email,
      String password,
      String confirmPassword,
      String firstName,
      String lastName,
      String displayName,
      String role,
      Set<String> subjects,
      String experience,
      String schoolName) {
    super(email, password, confirmPassword, firstName, lastName, displayName, role);
    this.subjects = subjects;
    this.experience = experience;
    this.schoolName = schoolName;
  }
}
