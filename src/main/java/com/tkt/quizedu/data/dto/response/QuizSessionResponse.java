package com.tkt.quizedu.data.dto.response;

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
public class QuizSessionResponse {
  String id;
  String quizId;
  String classId;
  String teacherId;
  String status;
  String accessCode;
  String startTime;
  String endTime;
}
