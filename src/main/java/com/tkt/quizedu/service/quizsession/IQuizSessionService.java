package com.tkt.quizedu.service.quizsession;

import com.tkt.quizedu.data.dto.request.QuizSessionRequest;
import com.tkt.quizedu.data.dto.response.AssessmentResponse;
import com.tkt.quizedu.data.dto.response.QuizSessionResponse;


public interface IQuizSessionService {
    QuizSessionResponse createQuizSession(QuizSessionRequest request);
    boolean joinQuizSession(String accessCode, String userId);
}
