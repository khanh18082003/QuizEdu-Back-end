package com.tkt.quizedu.data.dto.request;

import java.io.Serializable;

import jakarta.validation.constraints.NotBlank;

import com.tkt.quizedu.data.validator.LevelValid;

import lombok.*;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StudentCreationDTORequest extends UserCreationDTORequest implements Serializable {

  @NotBlank(message = "MESSAGE_NOT_BLANK")
  @LevelValid
  String level;

  @NotBlank(message = "MESSAGE_NOT_BLANK")
  String schoolName;

  public StudentCreationDTORequest(
      String email,
      String password,
      String confirmPassword,
      String firstName,
      String lastName,
      String displayName,
      String role,
      String level,
      String schoolName) {
    super(email, password, confirmPassword, firstName, lastName, displayName, role);
    this.level = level;
    this.schoolName = schoolName;
  }
}
