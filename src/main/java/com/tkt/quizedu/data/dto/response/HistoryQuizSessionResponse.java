package com.tkt.quizedu.data.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.tkt.quizedu.data.collection.MatchingQuiz;
import com.tkt.quizedu.data.collection.MultipleChoiceQuiz;
import com.tkt.quizedu.data.collection.Quiz;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class HistoryQuizSessionResponse {
  private Quiz quiz;
  private MultipleChoiceQuiz multipleChoiceQuiz;
  private MatchingQuiz matchingQuiz;
}
