package com.tkt.quizedu.data.dto.response;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OutboundUserResponse implements Serializable {
  String id;
  String email;
  boolean verifiedEmail;
  String name;
  String givenName;
  String familyName;
  String picture;
}
