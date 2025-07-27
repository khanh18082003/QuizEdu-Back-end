package com.tkt.quizedu.service.quizsession;

import org.springframework.stereotype.Service;

import com.tkt.quizedu.data.collection.*;
import com.tkt.quizedu.data.constant.ErrorCode;
import com.tkt.quizedu.data.constant.SessionStatus;
import com.tkt.quizedu.data.dto.request.QuizSessionRequest;
import com.tkt.quizedu.data.dto.request.SubmitQuizRequest;
import com.tkt.quizedu.data.dto.response.HistoryQuizSessionResponse;
import com.tkt.quizedu.data.dto.response.QuizSessionDetailResponse;
import com.tkt.quizedu.data.dto.response.QuizSessionResponse;
import com.tkt.quizedu.data.dto.response.UserBaseResponse;
import com.tkt.quizedu.data.mapper.QuizSessionMapper;
import com.tkt.quizedu.data.mapper.UserMapper;
import com.tkt.quizedu.data.repository.*;
import com.tkt.quizedu.exception.QuizException;
import com.tkt.quizedu.service.quiz.IQuizService;
import com.tkt.quizedu.utils.GenerateVerificationCode;
import com.tkt.quizedu.utils.SecurityUtils;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Slf4j(topic = "QUIZ-SESSION-SERVICE")
public class QuizSessionServiceImpl implements IQuizSessionService {

  QuizSessionRepository quizSessionRepository;
  QuizSessionMapper quizSessionMapper;
  MultipleChoiceQuizRepository multipleChoiceQuizRepository;
  MatchingQuizRepository matchingQuizRepository;
  IQuizService quizService;
  QuizRepository quizRepository;
  UserRepository userRepository;
  UserMapper userMapper;

  @Override
  public QuizSessionResponse createQuizSession(QuizSessionRequest request) {
    QuizSession quizSession = quizSessionMapper.toQuizSession(request);
    String accessCode = GenerateVerificationCode.generateCode();
    while (quizSessionRepository.existsByAccessCodeAndStatus(accessCode, SessionStatus.LOBBY)) {
      accessCode = GenerateVerificationCode.generateCode();
    }
    quizSession.setAccessCode(accessCode);
    quizSession.setStatus(SessionStatus.LOBBY);
    return quizSessionMapper.toResponse(quizSessionRepository.save(quizSession));
  }

  @Override
  public boolean joinQuizSession(String accessCode) {
    QuizSession quizSession =
        quizSessionRepository.findByAccessCodeAndStatus(accessCode, SessionStatus.LOBBY);

    if (quizSession.getParticipants().stream()
        .anyMatch(p -> p.getUserId().equals(SecurityUtils.getUserDetail().getUser().getId()))) {
      return false; // User already joined
    }

    quizSession
        .getParticipants()
        .add(new QuizSession.Participant(SecurityUtils.getUserDetail().getUser().getId()));
    quizSessionRepository.save(quizSession);
    return true;
  }

  @Override
  public int submitQuizSession(SubmitQuizRequest request) {
    int pointOfMultipleChoiceQuiz =
        quizService.evaluateMultipleChoiceQuizQuestion(
            request.quizSessionId(), request.multipleChoiceAnswers());
    int pointOfMatchingQuiz =
        quizService.evaluateMatchingQuizQuestion(
            request.quizSessionId(), request.matchingAnswers());
    System.out.println("Point of Multiple Choice Quiz: " + pointOfMultipleChoiceQuiz);
    System.out.println("Point of Matching Quiz: " + pointOfMatchingQuiz);
    return pointOfMultipleChoiceQuiz + pointOfMatchingQuiz;
  }

  @Override
  public HistoryQuizSessionResponse getQuizSessionHistory(String quizSessionId, String userId) {
    QuizSession quizSession =
        quizSessionRepository
            .findById(quizSessionId)
            .orElseThrow(() -> new IllegalArgumentException("Quiz session not found"));

    HistoryQuizSessionResponse response = new HistoryQuizSessionResponse();
    Quiz quiz =
        quizRepository
            .findById(quizSession.getQuizId())
            .orElseThrow(() -> new IllegalArgumentException("Quiz not found"));

    MultipleChoiceQuiz multipleChoiceQuiz =
        multipleChoiceQuizRepository.findByQuizId(quizSession.getQuizId());
    multipleChoiceQuiz.setQuestions(
        quizService.getMultipleChoiceHistoryByUserId(quizSessionId, userId));

    MatchingQuiz matchingQuiz = matchingQuizRepository.findByQuizId(quizSession.getQuizId());
    matchingQuiz.setAnswerParticipants(
        quizService.getMatchingHistoryByUserId(quizSessionId, userId));

    response.setQuiz(quiz);
    response.setMultipleChoiceQuiz(multipleChoiceQuiz);
    response.setMatchingQuiz(matchingQuiz);
    return response;
  }

  @Override
  public QuizSessionDetailResponse getQuizSessionDetail(String quizSessionId) {
    QuizSession quizSession =
        quizSessionRepository
            .findById(quizSessionId)
            .orElseThrow(() -> new QuizException(ErrorCode.MESSAGE_INVALID_ID));
    User teacher =
        userRepository
            .findById(quizSession.getTeacherId())
            .orElseThrow(() -> new QuizException(ErrorCode.MESSAGE_INVALID_ID));
    UserBaseResponse teacherResponse = userMapper.toUserBaseResponse(teacher);
    MatchingQuiz matchingQuiz = matchingQuizRepository.findByQuizId(quizSession.getQuizId());
    MultipleChoiceQuiz multipleChoiceQuiz =
        multipleChoiceQuizRepository.findByQuizId(quizSession.getQuizId());
    int totalQuestions =
        multipleChoiceQuiz.getQuestions().size() + (matchingQuiz.getMatchPairs().size() != 0 ? 1 : 0);
    return QuizSessionDetailResponse.builder()
        .id(quizSession.getId())
        .teacher(teacherResponse)
        .startTime(quizSession.getStartTime())
        .totalQuestions(totalQuestions)
        .status(quizSession.getStatus())
        .build();
  }
}
