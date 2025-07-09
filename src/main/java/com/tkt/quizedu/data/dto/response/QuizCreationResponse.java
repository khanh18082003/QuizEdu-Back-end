package com.tkt.quizedu.data.dto.response;

import com.tkt.quizedu.data.collection.MultipleChoiceQuiz;
import com.tkt.quizedu.data.collection.Quiz;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class QuizCreationResponse {
    private Quiz quiz;
    private MultipleChoiceQuiz multipleChoiceQuiz;
}
