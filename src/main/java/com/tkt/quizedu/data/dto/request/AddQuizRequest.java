package com.tkt.quizedu.data.dto.request;

import com.tkt.quizedu.data.constant.QuizType;

import lombok.Builder;

@Builder
public record AddQuizRequest(
    QuizType type,
    MultipleChoiceQuizRequest multipleChoiceQuizRequest,
    MatchingQuizRequest matchingQuizRequest) {}
