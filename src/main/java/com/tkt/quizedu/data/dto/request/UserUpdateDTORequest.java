package com.tkt.quizedu.data.dto.request;

import java.io.Serializable;

import lombok.*;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PROTECTED)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateDTORequest implements Serializable {
  String firstName;
  String lastName;
  String displayName;
}
