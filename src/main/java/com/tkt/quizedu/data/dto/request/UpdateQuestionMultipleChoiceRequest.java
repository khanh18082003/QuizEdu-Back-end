package com.tkt.quizedu.data.dto.request;

import com.tkt.quizedu.data.collection.MultipleChoiceQuiz;
import lombok.Builder;

import java.util.List;
import java.util.UUID;

@Builder
public record UpdateQuestionMultipleChoiceRequest(
        UUID questionId,
        String questionText,
        String hint,
        int timeLimit,
        boolean allowMultipleAnswers,
        int points,
        List<MultipleChoiceQuiz.Question.AnswerOption> answers
) {
}
