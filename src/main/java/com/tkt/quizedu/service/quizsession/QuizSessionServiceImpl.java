package com.tkt.quizedu.service.quizsession;

import com.tkt.quizedu.data.collection.Quiz;
import com.tkt.quizedu.data.collection.QuizSession;
import com.tkt.quizedu.data.dto.request.QuizSessionRequest;
import com.tkt.quizedu.data.dto.response.AssessmentResponse;
import com.tkt.quizedu.data.dto.response.QuizSessionResponse;
import com.tkt.quizedu.data.mapper.QuizSessionMapper;
import com.tkt.quizedu.data.repository.MultipleChoiceQuizRepository;
import com.tkt.quizedu.data.repository.QuizRepository;
import com.tkt.quizedu.data.repository.QuizSessionRepository;
import com.tkt.quizedu.utils.GenerateVerificationCode;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Slf4j(topic = "QUIZ-SESSION-SERVICE")
public class QuizSessionServiceImpl implements IQuizSessionService {

    QuizSessionRepository quizSessionRepository;
    QuizSessionMapper quizSessionMapper;
    MultipleChoiceQuizRepository multipleChoiceQuizRepository;
    QuizRepository quizRepository;
    @Override
    public QuizSessionResponse createQuizSession(QuizSessionRequest request) {
        QuizSession quizSession = quizSessionMapper.toQuizSession(request);
        quizSession.setStartTime(LocalDateTime.now());
        quizSession.setAccessCode(GenerateVerificationCode.generateCode());
        return quizSessionMapper.toResponse(quizSessionRepository.save(quizSession));
    }
    @Override
    public boolean joinQuizSession(String accessCode, String userId) {
        QuizSession quizSession = quizSessionRepository.findByAccessCode(accessCode);

        if (quizSession.getParticipants().stream().anyMatch(p -> p.getUserId().equals(userId))) {
            return false; // User already joined
        }

        quizSession.getParticipants().add(new QuizSession.Participant(userId, LocalDateTime.now()));
        quizSessionRepository.save(quizSession);
        return true;
    }
}
