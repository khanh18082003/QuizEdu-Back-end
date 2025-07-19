package com.tkt.quizedu.data.dto.response;

import java.util.List;
import java.util.UUID;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class MultipleChoiceQuizResponse {
  private String id;
  private String quizId;
  private List<QuestionResponse> questions;

  @Builder
  @Data
  public static class QuestionResponse {
    private UUID questionId;
    private String questionText;
    private String hint;
    private int timeLimit;
    private boolean allowMultipleAnswers;
    private int points;
    private List<AnswerOptionResponse> answers;
  }

  @Builder
  @Data
  public static class AnswerOptionResponse {
    private String answerText;
    private boolean correct;
  }
}
