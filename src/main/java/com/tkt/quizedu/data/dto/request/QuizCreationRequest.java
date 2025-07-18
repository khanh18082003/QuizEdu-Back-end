package com.tkt.quizedu.data.dto.request;

import java.util.List;

import com.tkt.quizedu.data.collection.MatchingQuiz;
import com.tkt.quizedu.data.collection.MultipleChoiceQuiz;

import lombok.Builder;

@Builder
public record QuizCreationRequest(
    String name,
    String description,
    String subjectId,
    List<String> classIds,
    boolean isActive,
    MultipleChoiceQuizRequest multipleChoiceQuiz,
    MatchingQuizRequest matchingQuiz
    // còn các loại quiz khác
    ) {}
