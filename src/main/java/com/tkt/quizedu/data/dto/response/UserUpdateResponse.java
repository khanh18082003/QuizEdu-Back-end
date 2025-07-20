package com.tkt.quizedu.data.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateResponse {
  String email;
  String firstName;
  String lastName;
  String displayName;
}
