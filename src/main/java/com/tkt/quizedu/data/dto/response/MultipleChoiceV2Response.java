package com.tkt.quizedu.data.dto.response;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

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
public class MultipleChoiceV2Response implements Serializable {
  private String id;
  private String quizId;
  private List<MultipleChoiceV2Response.QuestionResponse> questions;

  @Builder
  @Data
  public static class QuestionResponse {
    private UUID questionId;
    private String questionText;
    private String hint;
    private int timeLimit;
    private boolean allowMultipleAnswers;
    private int points;
    private List<MultipleChoiceV2Response.AnswerOptionResponse> answers;
  }

  @Builder
  @Data
  public static class AnswerOptionResponse {
    private String answerText;
  }
}
