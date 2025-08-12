package com.tkt.quizedu.service.quizsession;

import java.util.List;

import com.tkt.quizedu.data.dto.request.QuizSessionRequest;
import com.tkt.quizedu.data.dto.request.SubmitQuizRequest;
import com.tkt.quizedu.data.dto.response.*;

public interface IQuizSessionService {
  QuizSessionResponse createQuizSession(QuizSessionRequest request);

  void joinQuizSession(String accessCode);

  int submitQuizSession(SubmitQuizRequest request);

  HistoryQuizSessionResponse getQuizSessionHistory(String quizSessionId, String userId);

  QuizSessionDetailResponse getQuizSessionDetail(String quizSessionId);

  void startQuizSession(String quizSessionId);

  void closeQuizSession(String quizSessionId);

  List<UserBaseResponse> getStudentsInQuizSession(String quizSessionId);

  List<UserSubmitResponse> getScoreboard(String quizSessionId);

  List<ScoreQuizSessionStudentResponse> getScoresByStudentId();
}
