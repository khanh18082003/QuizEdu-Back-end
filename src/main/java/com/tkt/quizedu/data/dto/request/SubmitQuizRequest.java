package com.tkt.quizedu.data.dto.request;

import com.tkt.quizedu.data.collection.MatchingQuiz;

import java.util.List;

public record SubmitQuizRequest(
    String quizSessionId,
    List<SubmitMultipleChoiceRequest> multipleChoiceAnswers,
    List<MatchingQuiz.AnswerPair> matchingAnswers
) {
}
