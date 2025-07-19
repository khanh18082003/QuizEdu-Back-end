package com.tkt.quizedu.data.dto.request;

import java.util.List;

import lombok.Builder;

@Builder
public record MatchingQuizRequest(int timeLimit, List<MatchingQuestionRequest> questions) {}
