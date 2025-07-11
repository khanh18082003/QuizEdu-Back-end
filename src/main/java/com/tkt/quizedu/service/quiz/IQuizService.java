package com.tkt.quizedu.service.quiz;

import com.tkt.quizedu.data.dto.request.DeleteQuestionMultipleChoiceRequest;
import com.tkt.quizedu.data.dto.request.QuestionMultipleChoiceRequest;
import com.tkt.quizedu.data.dto.request.QuizCreationRequest;
import com.tkt.quizedu.data.dto.request.UpdateQuestionMultipleChoiceRequest;
import com.tkt.quizedu.data.dto.response.QuizCreationResponse;

import java.util.List;
import java.util.UUID;

public interface IQuizService {
    QuizCreationResponse save(QuizCreationRequest request);
    List<QuizCreationResponse> getAll();
    QuizCreationResponse getById(String id);
    void delete(String id);
    QuizCreationResponse addMultipleChoiceQuizQuestion(String quizId, List<QuestionMultipleChoiceRequest> questions);
    void deleteMultipleChoiceQuizQuestion(String quizId, List<UUID> request);
    QuizCreationResponse updateMultipleChoiceQuizQuestion(
            String quizId,
            List<UpdateQuestionMultipleChoiceRequest> questions
    );

}
