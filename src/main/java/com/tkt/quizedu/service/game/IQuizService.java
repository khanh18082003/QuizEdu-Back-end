package com.tkt.quizedu.service.game;

import com.tkt.quizedu.data.dto.request.QuizCreationRequest;
import com.tkt.quizedu.data.dto.response.QuizCreationResponse;

public interface IQuizService {
    QuizCreationResponse save(QuizCreationRequest request);
}
