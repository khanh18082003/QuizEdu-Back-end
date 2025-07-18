package com.tkt.quizedu.data.mapper;

import com.tkt.quizedu.data.collection.Quiz;
import com.tkt.quizedu.data.dto.request.QuizCreationRequest;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface QuizMapper {
    Quiz toQuiz(QuizCreationRequest req);
}
