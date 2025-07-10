package com.tkt.quizedu.data.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.List;

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
  LocalDate createdAt;
  boolean isActive;
}
