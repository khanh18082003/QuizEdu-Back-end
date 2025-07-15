package com.tkt.quizedu.data.dto.request;

import java.io.Serializable;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import com.tkt.quizedu.data.validator.RoleValid;

import lombok.*;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserCreationDTORequest implements Serializable {
  @Email(message = "MESSAGE_INVALID_EMAIL")
  @NotBlank(message = "MESSAGE_NOT_BLANK")
  String email;

  @NotBlank(message = "MESSAGE_NOT_BLANK")
  @Size(min = 8, max = 20, message = "MESSAGE_PASSWORD_SIZE")
  @Pattern(
      regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*])[A-Za-z\\d!@#$%^&*]{8,}$",
      message = "MESSAGE_INVALID_FORMAT_PASSWORD")
  String password;

  @NotBlank(message = "MESSAGE_NOT_BLANK")
  String confirmPassword;

  @NotBlank(message = "MESSAGE_NOT_BLANK")
  String firstName;

  @NotBlank(message = "MESSAGE_NOT_BLANK")
  String lastName;

  @NotBlank(message = "MESSAGE_NOT_BLANK")
  String displayName;

  @RoleValid String role;

  public UserCreationDTORequest(String email, String encode, String displayName, String role) {
    this.email = email;
    this.password = encode;
    this.displayName = displayName;
    this.role = role;
  }
}
