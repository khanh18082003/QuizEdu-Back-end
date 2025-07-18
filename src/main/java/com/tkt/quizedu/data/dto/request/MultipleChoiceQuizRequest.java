package com.tkt.quizedu.data.dto.request;

import lombok.Builder;

import java.util.List;

@Builder
public record MultipleChoiceQuizRequest(
        List<QuestionMultipleChoiceRequest> questions
) {
}
