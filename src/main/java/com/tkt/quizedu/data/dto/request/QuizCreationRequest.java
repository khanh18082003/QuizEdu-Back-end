package com.tkt.quizedu.data.dto.request;

import lombok.Builder;

@Builder
public record QuizCreationRequest(
    String name,
    String description,
    boolean isActive,
    boolean isPublic,
    MultipleChoiceQuizRequest multipleChoiceQuiz,
    MatchingQuizRequest matchingQuiz
    // còn các loại quiz khác
    ) {}
