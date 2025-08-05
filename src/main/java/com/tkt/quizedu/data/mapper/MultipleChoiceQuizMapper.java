package com.tkt.quizedu.data.mapper;

import java.util.UUID;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.tkt.quizedu.data.collection.MultipleChoiceQuiz;
import com.tkt.quizedu.data.dto.request.MultipleChoiceQuizRequest;
import com.tkt.quizedu.data.dto.response.MultipleChoiceQuizResponse;
import com.tkt.quizedu.data.dto.response.MultipleChoiceV2Response;

@Mapper(componentModel = "spring")
public interface MultipleChoiceQuizMapper {
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "questions[].answerParticipants", ignore = true)
  MultipleChoiceQuiz toMultipleChoiceQuiz(MultipleChoiceQuizRequest req);

  @AfterMapping
  default void assignUUIDs(
      MultipleChoiceQuizRequest request, @MappingTarget MultipleChoiceQuiz quiz) {
    for (int i = 0; i < quiz.getQuestions().size(); i++) {
      var question = quiz.getQuestions().get(i);
      if (question.getQuestionId() == null) {
        question.setQuestionId(UUID.randomUUID());
      }
    }
  }

  MultipleChoiceQuizResponse toMultipleChoiceQuizResponse(MultipleChoiceQuiz quiz);

  MultipleChoiceV2Response toMultipleChoiceV2Response(MultipleChoiceQuiz quiz);
}
