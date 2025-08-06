package com.tkt.quizedu.data.dto.response;

import java.time.LocalDateTime;

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
public class CommentResponse {
  String id;
  UserBaseResponse user;
  String content;
  LocalDateTime createdAt;
  LocalDateTime updatedAt;
}
