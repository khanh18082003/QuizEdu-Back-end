package com.tkt.quizedu.service.quiz;

import java.util.List;
import java.util.UUID;

import com.tkt.quizedu.data.collection.MatchingQuiz;
import com.tkt.quizedu.data.dto.request.*;
import com.tkt.quizedu.data.dto.response.PaginationResponse;
import com.tkt.quizedu.data.dto.response.QuizResponse;

public interface IQuizService {
  QuizResponse save(QuizCreationRequest request);

  PaginationResponse<QuizResponse> getAll(int page, int pageSize);

  QuizResponse getById(String id);

  void delete(String id);

  QuizResponse addMultipleChoiceQuizQuestion(
      String quizId, List<QuestionMultipleChoiceRequest> questions);

  void deleteMultipleChoiceQuizQuestion(String quizId, List<UUID> request);

  int evaluateMultipleChoiceQuizQuestion(String quizId, List<SubmitMultipleChoiceRequest> request);

  int evaluateMatchingQuizQuestion(String quizId, MatchingQuiz.UserAnswer request);

  QuizResponse updateMultipleChoiceQuizQuestion(
      String quizId, List<UpdateQuestionMultipleChoiceRequest> questions);

  // Matching
  QuizResponse addMatchingQuizQuestion(String quizId, List<MatchingQuestionRequest> questions);

  void deleteMatchingQuizQuestion(String quizId, List<UUID> request);

  QuizResponse updateMatchingQuizQuestion(
      String quizId, UpdateMatchingQuestionRequest request);

  QuizResponse addQuizQuestion(String quizId, AddQuizRequest request);
}
