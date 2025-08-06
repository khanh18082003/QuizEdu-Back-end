package com.tkt.quizedu.data.dto.response;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.tkt.quizedu.data.constant.SessionStatus;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class QuizDetailResponse implements Serializable {
  String id;
  String name;
  String description;
  String quizSessionId;
  String accessCode;
  SessionStatus status;
  LocalDateTime startTime;
  LocalDateTime endTime;
  LocalDateTime createdAt;
}
