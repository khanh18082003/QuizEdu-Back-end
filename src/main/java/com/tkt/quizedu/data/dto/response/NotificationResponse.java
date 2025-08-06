package com.tkt.quizedu.data.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.tkt.quizedu.data.collection.ClassRoom;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NotificationResponse {
  String id;
  String description;
  UserBaseResponse teacher;
  ClassRoom classRoom;
  List<String> xPathFiles;
  LocalDateTime createdAt;
  LocalDateTime updatedAt;
  List<CommentResponse> comments;
}
