package com.tkt.quizedu.data.dto.request;

import com.tkt.quizedu.data.collection.MultipleChoiceQuiz;
import lombok.Builder;

import java.util.List;

@Builder
public record QuizCreationRequest(
        String name,
        String description,
        String teacherId,
        String subjectId,
        List<String> classIds,
        boolean isActive,
        MultipleChoiceQuiz multipleChoiceQuiz
) {
}
