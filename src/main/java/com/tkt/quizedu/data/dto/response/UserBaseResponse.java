package com.tkt.quizedu.data.dto.response;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.tkt.quizedu.data.constant.UserRole;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PROTECTED)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserBaseResponse implements Serializable {
  String id;
  String email;
  String firstName;
  String lastName;
  String displayName;
  String avatar;
  Boolean noPassword;
  boolean isActive;
  UserRole role;
  LocalDateTime createdAt;
  LocalDateTime updatedAt;
}
