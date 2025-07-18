package com.tkt.quizedu.data.dto.response;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ExchangeTokenResponse implements Serializable {
  String accessToken;
  Long expiresIn;
  String scope;
  String tokenType;
  String idToken;
}
