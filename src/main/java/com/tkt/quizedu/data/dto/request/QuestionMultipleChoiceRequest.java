package com.tkt.quizedu.data.dto.request;

import java.util.List;

import com.tkt.quizedu.data.collection.MultipleChoiceQuiz;

import lombok.Builder;

@Builder
public record QuestionMultipleChoiceRequest(
    String questionText,
    String hint,
    int timeLimit,
    boolean allowMultipleAnswers,
    int points,
    List<MultipleChoiceQuiz.Question.AnswerOption> answers) {}
