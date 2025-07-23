package com.tkt.quizedu.data.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public class ClassroomDetailResponse {
  String id;
  String name;
  String description;
  String classCode;
  LocalDateTime createdAt;
  List<QuizBaseResponse> quiz;
  List<UserBaseResponse> students;
}
