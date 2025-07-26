package com.tkt.quizedu.service.quizsession;

import com.tkt.quizedu.data.dto.request.QuizSessionRequest;
import com.tkt.quizedu.data.dto.request.SubmitQuizRequest;
import com.tkt.quizedu.data.dto.response.HistoryQuizSessionResponse;
import com.tkt.quizedu.data.dto.response.QuizSessionResponse;

public interface IQuizSessionService {
  QuizSessionResponse createQuizSession(QuizSessionRequest request);

  boolean joinQuizSession(String accessCode);

  int submitQuizSession(SubmitQuizRequest request);

  HistoryQuizSessionResponse getQuizSessionHistory(String quizSessionId, String userId);
}
