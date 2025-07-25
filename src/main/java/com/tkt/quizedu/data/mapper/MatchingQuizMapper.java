package com.tkt.quizedu.data.mapper;

import java.util.List;
import java.util.UUID;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.tkt.quizedu.data.collection.MatchingQuiz;
import com.tkt.quizedu.data.dto.request.MatchingQuestionRequest;
import com.tkt.quizedu.data.dto.request.MatchingQuizRequest;
import com.tkt.quizedu.data.dto.response.MatchingQuizResponse;

@Mapper(componentModel = "spring")
public interface MatchingQuizMapper {
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "answerParticipants", ignore = true)
  MatchingQuiz toMatchingQuiz(MatchingQuizRequest req);

  @AfterMapping
  default void assignUUIDs(MatchingQuizRequest request, @MappingTarget MatchingQuiz quiz) {
    if (quiz.getMatchPairs() != null) {
      for (int i = 0; i < quiz.getMatchPairs().size(); i++) {
        var pair = quiz.getMatchPairs().get(i);
        if (pair.getId() == null) {
          pair.setId(UUID.randomUUID());
        }
      }
    }
  }

  // Map từ MatchingQuestionRequest -> MatchPair (nested)
  @Mapping(target = "id", expression = "java(java.util.UUID.randomUUID())")
  @Mapping(target = "itemA.content", source = "contentA")
  @Mapping(target = "itemA.matchingType", source = "typeA")
  @Mapping(target = "itemB.content", source = "contentB")
  @Mapping(target = "itemB.matchingType", source = "typeB")
  MatchingQuiz.MatchPair toMatchPair(MatchingQuestionRequest request);

  List<MatchingQuiz.MatchPair> toMatchPairList(List<MatchingQuestionRequest> requests);

  @Mapping(target = "questions", source = "matchPairs")
  MatchingQuizResponse toMatchingQuizResponse(MatchingQuiz quiz);
}
