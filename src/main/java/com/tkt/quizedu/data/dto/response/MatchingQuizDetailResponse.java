package com.tkt.quizedu.data.dto.response;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.tkt.quizedu.data.constant.MatchingType;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MatchingQuizDetailResponse implements Serializable {
  String id;
  String quizId;
  int timeLimit;
  List<MatchItemResponse> itemA;
  List<MatchItemResponse> itemB;

  @Builder
  @Getter
  @Setter
  @JsonInclude(JsonInclude.Include.NON_NULL)
  public static class MatchItemResponse {
    private UUID id;
    private String content;
    private MatchingType matchingType;
  }
}
