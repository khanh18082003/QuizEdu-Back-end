package com.tkt.quizedu.data.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.tkt.quizedu.data.collection.Quiz;
import com.tkt.quizedu.data.dto.request.QuizCreationRequest;
import com.tkt.quizedu.data.dto.response.QuizBaseResponse;

@Mapper(componentModel = "spring")
public interface QuizMapper {
  Quiz toQuiz(QuizCreationRequest req);

  @Mapping(target = "isActive", source = "active")
  QuizBaseResponse toQuizBaseResponse(Quiz quiz);
}
