package com.tkt.quizedu.data.dto.response;

import com.tkt.quizedu.data.collection.MatchingQuiz;
import com.tkt.quizedu.data.collection.MultipleChoiceQuiz;
import com.tkt.quizedu.data.collection.Quiz;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class QuizResponse {
  private Quiz quiz;
  private MultipleChoiceQuizResponse multipleChoiceQuiz;
  private MatchingQuizResponse matchingQuiz;
  // còn các loại quiz khác
}
