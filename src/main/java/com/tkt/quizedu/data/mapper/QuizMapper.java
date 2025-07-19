package com.tkt.quizedu.data.mapper;

import org.mapstruct.Mapper;

import com.tkt.quizedu.data.collection.Quiz;
import com.tkt.quizedu.data.dto.request.QuizCreationRequest;

@Mapper(componentModel = "spring")
public interface QuizMapper {
  Quiz toQuiz(QuizCreationRequest req);
}
