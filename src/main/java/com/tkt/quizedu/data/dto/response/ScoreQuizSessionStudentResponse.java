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
@FieldDefaults(level = AccessLevel.PROTECTED)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public class ScoreQuizSessionStudentResponse implements Serializable {
  Integer score;
  String quizSessionId;
  String quizId;
  String quizName;
  String classroomId;
  String classroomName;
  LocalDateTime startTime;
  LocalDateTime endTime;
}
