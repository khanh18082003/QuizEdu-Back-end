package com.tkt.quizedu.data.dto.request;

import java.util.List;

import com.tkt.quizedu.data.collection.MultipleChoiceQuiz;

public record SubmitMultipleChoiceRequest(
    String questionId, List<MultipleChoiceQuiz.Question.AnswerParticipant> answerParticipant) {}
