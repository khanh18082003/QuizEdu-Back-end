package com.tkt.quizedu.data.dto.response;

import java.util.List;
import java.util.UUID;

import com.tkt.quizedu.data.constant.MatchingType;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class MatchingQuizResponse {
  private String id;
  private String quizId;
  private int timeLimit;
  private List<MatchPairResponse> questions;

  @Builder
  @Data
  public static class MatchPairResponse {
    private UUID id;
    private MatchItemResponse itemA;
    private MatchItemResponse itemB;
    private int points;
  }

  @Builder
  @Data
  public static class MatchItemResponse {
    private String content;
    private MatchingType matchingType;
  }
}
