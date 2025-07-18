package com.tkt.quizedu.data.dto.request;

import java.io.Serializable;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
public class ExchangeTokenRequest implements Serializable {
  String code;
  String clientId;
  String clientSecret;
  String redirectUri;
  String grantType;
}
