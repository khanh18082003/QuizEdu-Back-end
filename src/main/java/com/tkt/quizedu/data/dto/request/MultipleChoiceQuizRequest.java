package com.tkt.quizedu.data.dto.request;

import java.util.List;

import lombok.Builder;

@Builder
public record MultipleChoiceQuizRequest(List<QuestionMultipleChoiceRequest> questions) {}
