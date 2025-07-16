package com.tkt.quizedu.data.mapper;

import com.tkt.quizedu.data.collection.MatchingQuiz;
import com.tkt.quizedu.data.dto.request.MatchingQuestionRequest;
import com.tkt.quizedu.data.dto.request.MatchingQuizRequest;
import com.tkt.quizedu.data.dto.response.MatchingQuizResponse;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;
import java.util.UUID;

@Mapper(componentModel = "spring")
public interface MatchingQuizMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "answerParticipants", ignore = true)
    MatchingQuiz toMatchingQuiz(MatchingQuizRequest req);

    @AfterMapping
    default void assignUUIDs(MatchingQuizRequest request, @MappingTarget MatchingQuiz quiz) {
        if (quiz.getQuestions() != null) {
            for (int i = 0; i < quiz.getQuestions().size(); i++) {
                var pair = quiz.getQuestions().get(i);
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
    MatchingQuizResponse toMatchingQuizResponse(MatchingQuiz quiz);
}
