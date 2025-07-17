package com.tkt.quizedu.data.collection;

import java.io.Serial;
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

  List<MatchPair> questions;
  List<UserAnswer> answerParticipants;

  @Data
  public static class MatchPair {
    UUID id;
    MatchItem itemA;
    MatchItem itemB;
    int points;
  }

  @Data
  public static class MatchItem {
    String content;
    MatchingType matchingType;
  }

  @Data
  public static class UserAnswer {
    String userId;
    List<AnswerPair> answers;
  }

  @Data
  public static class AnswerPair {
    UUID matchPairId;
    MatchItem itemA;
    MatchItem itemB;
    boolean isCorrect;
  }
}
