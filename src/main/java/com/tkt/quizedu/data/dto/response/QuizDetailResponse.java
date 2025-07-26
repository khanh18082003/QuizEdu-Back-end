package com.tkt.quizedu.data.dto.response;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonInclude;

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
  boolean isActive;
  String quizSessionId;
  LocalDateTime startTime;
  LocalDateTime endTime;
}
