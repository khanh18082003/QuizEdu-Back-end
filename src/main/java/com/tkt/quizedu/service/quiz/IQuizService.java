package com.tkt.quizedu.service.quiz;

import java.util.List;
import java.util.UUID;

import com.tkt.quizedu.data.dto.request.*;
import com.tkt.quizedu.data.dto.response.QuizResponse;

public interface IQuizService {
  QuizResponse save(QuizCreationRequest request);

  List<QuizResponse> getAll();

  QuizResponse getById(String id);

  void delete(String id);

  QuizResponse addMultipleChoiceQuizQuestion(
      String quizId, List<QuestionMultipleChoiceRequest> questions);

  void deleteMultipleChoiceQuizQuestion(String quizId, List<UUID> request);

  QuizResponse updateMultipleChoiceQuizQuestion(
      String quizId, List<UpdateQuestionMultipleChoiceRequest> questions);
  //Matching
  QuizResponse addMatchingQuizQuestion(
        String quizId, List<MatchingQuestionRequest> questions);
  void deleteMatchingQuizQuestion(String quizId, List<UUID> request);
    QuizResponse updateMatchingQuizQuestion(
        String quizId, List<UpdateMatchingQuestionRequest> questions);
  QuizResponse addQuizQuestion(
      String quizId, AddQuizRequest request);
}
