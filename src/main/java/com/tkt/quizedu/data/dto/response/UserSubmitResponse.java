package com.tkt.quizedu.data.dto.response;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PROTECTED)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public class UserSubmitResponse implements Serializable {
  String id;
  String email;
  String firstName;
  String lastName;
  Integer score;
  Integer rank;
  String quizSessionId;
}
