package com.tkt.quizedu.data.mapper;

import org.mapstruct.Mapper;

import com.tkt.quizedu.data.collection.QuizSession;
import com.tkt.quizedu.data.dto.request.QuizSessionRequest;
import com.tkt.quizedu.data.dto.response.QuizSessionResponse;

@Mapper(componentModel = "spring")
public interface QuizSessionMapper {
  QuizSession toQuizSession(QuizSessionRequest request);

  QuizSessionResponse toResponse(QuizSession quizSession);
}
