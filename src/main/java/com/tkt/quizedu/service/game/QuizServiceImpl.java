package com.tkt.quizedu.service.game;

import com.tkt.quizedu.data.collection.MultipleChoiceQuiz;
import com.tkt.quizedu.data.collection.Quiz;
import com.tkt.quizedu.data.dto.request.QuizCreationRequest;
import com.tkt.quizedu.data.dto.response.QuizCreationResponse;
import com.tkt.quizedu.data.repository.MultipleChoiceQuizRepository;
import com.tkt.quizedu.data.repository.QuizRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Slf4j(topic = "QUIZ-SERVICE")
public class QuizServiceImpl implements IQuizService {

    QuizRepository quizRepository;
    MultipleChoiceQuizRepository multipleChoiceQuizRepository;
    @Override
    public QuizCreationResponse save(QuizCreationRequest request) {
        Quiz quiz=Quiz.builder()
                .name(request.name())
                .description(request.description())
                .teacherId(request.teacherId())
                .subjectId(request.subjectId())
                .classIds(request.classIds())
                .isActive(request.isActive())
                .build();
        quiz= quizRepository.save(quiz);
        MultipleChoiceQuiz multipleChoiceQuiz=null;
        if (request.multipleChoiceQuiz() != null) {
            request.multipleChoiceQuiz().setQuizId(quiz.getId());
            multipleChoiceQuiz=multipleChoiceQuizRepository.save(request.multipleChoiceQuiz());
        }
        return QuizCreationResponse.builder()
                .quiz(quiz)
                .multipleChoiceQuiz(multipleChoiceQuiz)
                .build();
    }
}
