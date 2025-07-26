package com.tkt.quizedu.data.collection;

import java.io.Serial;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.tkt.quizedu.data.base.StringIdentityCollection;
import com.tkt.quizedu.data.constant.MatchingType;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Document(collection = "matchingQuiz")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class MatchingQuiz extends StringIdentityCollection {
  @Serial private static final long serialVersionUID = -5465733518693373245L;
  @Id String id;
  int timeLimit;
  String quizId;
  String quizSessionId;
  @Builder.Default List<MatchPair> matchPairs = new ArrayList<>();
  @Builder.Default List<UserAnswer> answerParticipants = new ArrayList<>();

  @Data
  public static class MatchPair {
    UUID id;
    MatchItem itemA;
    MatchItem itemB;
    int points;
  }

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public static class MatchItem {
    String content;
    MatchingType matchingType;
  }

  @Builder
  @Data
  public static class UserAnswer {
    String userId;
    @Builder.Default List<AnswerPair> answers = new ArrayList<>();
    String quizSessionId;
  }

  @Data
  public static class AnswerPair {
    UUID matchPairId;
    MatchItem itemA;
    MatchItem itemB;
    boolean isCorrect;
  }
}
