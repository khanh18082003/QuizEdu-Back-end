package com.tkt.quizedu.data.dto.response;

import java.time.LocalDateTime;
import java.util.List;

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
public class ClassRoomResponse implements java.io.Serializable {
  String id;
  String name;
  String description;
  String teacherId;
  String classCode;
  List<String> studentIds;
  List<String> assignedQuizIds;
  LocalDateTime createdAt;
  boolean isActive;
}
