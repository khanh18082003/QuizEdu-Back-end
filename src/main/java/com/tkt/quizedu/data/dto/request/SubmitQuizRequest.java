package com.tkt.quizedu.data.dto.request;

import java.util.List;

import com.tkt.quizedu.data.collection.MatchingQuiz;

public record SubmitQuizRequest(
    String quizSessionId,
    List<SubmitMultipleChoiceRequest> multipleChoiceAnswers,
    List<MatchingQuiz.AnswerPair> matchingAnswers) {}
