package com.tkt.quizedu.service.quizsession;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tkt.quizedu.component.WebSocketPublisher;
import com.tkt.quizedu.data.collection.*;
import com.tkt.quizedu.data.constant.ErrorCode;
import com.tkt.quizedu.data.constant.SessionStatus;
import com.tkt.quizedu.data.dto.request.QuizSessionRequest;
import com.tkt.quizedu.data.dto.request.SubmitQuizRequest;
import com.tkt.quizedu.data.dto.response.*;
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
  ClassRoomRepository classRoomRepository;
  UserMapper userMapper;
  KafkaTemplate<String, String> kafkaTemplate;
  WebSocketPublisher webSocketPublisher;

  @Override
  public QuizSessionResponse createQuizSession(QuizSessionRequest request) {
    QuizSession quizSession = quizSessionMapper.toQuizSession(request);
    String accessCode = GenerateVerificationCode.generateCode();
    while (quizSessionRepository.existsByAccessCodeAndStatus(accessCode, SessionStatus.LOBBY)) {
      accessCode = GenerateVerificationCode.generateCode();
    }
    quizSession.setAccessCode(accessCode);
    quizSession.setStatus(SessionStatus.LOBBY);

    ClassRoom classRoom =
        classRoomRepository
            .findById(quizSession.getClassId())
            .orElseThrow(() -> new QuizException(ErrorCode.MESSAGE_INVALID_ID));
    if (!classRoom.getTeacherId().equals(quizSession.getTeacherId())) {
      throw new QuizException(ErrorCode.MESSAGE_INVALID_ID);
    }
    Quiz quiz =
        quizRepository
            .findById(quizSession.getQuizId())
            .orElseThrow(() -> new QuizException(ErrorCode.MESSAGE_INVALID_ID));
    List<User> students = userRepository.findAllById(classRoom.getStudentIds());
    String emailList = String.join(",", students.stream().map(User::getEmail).toList());
    String message =
        String.format("email=%s;accessCode=%s;quizName=%s", emailList, accessCode, quiz.getName());
    kafkaTemplate.send("send-access-code-to-emails", message);
    return quizSessionMapper.toResponse(quizSessionRepository.save(quizSession));
  }

  @Override
  @Transactional
  public void joinQuizSession(String accessCode) {
    CustomUserDetail userDetail = SecurityUtils.getUserDetail();
    if (userDetail == null) {
      throw new QuizException(ErrorCode.MESSAGE_UNAUTHORIZED);
    }
    User user = userDetail.getUser();
    QuizSession quizSession =
        quizSessionRepository
            .findByAccessCode(accessCode)
            .orElseThrow(() -> new QuizException(ErrorCode.MESSAGE_INVALID_ID));

    if (quizSession.getStatus() != SessionStatus.LOBBY) {
      throw new QuizException(ErrorCode.MESSAGE_INVALID_SESSION_STATUS);
    }

    if (quizSession.getParticipants().stream().anyMatch(p -> p.getUserId().equals(user.getId()))) {
      throw new QuizException(ErrorCode.MESSAGE_ALREADY_JOINED);
    }

    quizSession
        .getParticipants()
        .add(new QuizSession.Participant(SecurityUtils.getUserDetail().getUser().getId()));
    quizSessionRepository.save(quizSession);

    webSocketPublisher.publishJoinQuizSession(
        quizSession.getId(), userMapper.toUserBaseResponse(user));
  }

  @Override
  @Transactional
  public int submitQuizSession(SubmitQuizRequest request) {
    // Validate request
    if (request == null || request.quizSessionId() == null) {
      throw new QuizException(ErrorCode.MESSAGE_INVALID_REQUEST);
    }

    // Authenticate user
    CustomUserDetail userDetail = SecurityUtils.getUserDetail();
    if (userDetail == null) {
      throw new QuizException(ErrorCode.MESSAGE_UNAUTHORIZED);
    }
    User user = userDetail.getUser();

    // Retrieve quiz session
    QuizSession quizSession =
        quizSessionRepository
            .findById(request.quizSessionId())
            .orElseThrow(() -> new QuizException(ErrorCode.MESSAGE_INVALID_ID));

    // Validate quiz session status
    if (quizSession.getStatus() != SessionStatus.ACTIVE) {
      throw new QuizException(ErrorCode.MESSAGE_INVALID_SESSION_STATUS);
    }

    // Check if user has already submitted
    QuizSession.Participant participant =
        quizSession.getParticipants().stream()
            .filter(p -> p.getUserId().equals(user.getId()))
            .findFirst()
            .orElseThrow(() -> new QuizException(ErrorCode.MESSAGE_NOT_PARTICIPATED));

    if (participant.getScore() != null && participant.getScore() > 0) {
      throw new QuizException(ErrorCode.MESSAGE_ALREADY_SUBMITTED);
    }

    // Evaluate answers
    int pointOfMultipleChoiceQuiz =
        quizService.evaluateMultipleChoiceQuizQuestion(
            quizSession.getId(), request.multipleChoiceAnswers());
    int pointOfMatchingQuiz =
        quizService.evaluateMatchingQuizQuestion(quizSession.getId(), request.matchingAnswers());
    int totalScore = pointOfMultipleChoiceQuiz + pointOfMatchingQuiz;

    // Update participant score
    participant.setScore(totalScore);

    // Save updated quiz session
    quizSessionRepository.save(quizSession);

    // Publish submission event
    webSocketPublisher.publishSubmitQuizSession(
        request.quizSessionId(),
        UserSubmitResponse.builder()
            .id(user.getId())
            .email(user.getEmail())
            .quizSessionId(request.quizSessionId())
            .score(totalScore)
            .build());

    log.info(
        "User {} submitted quiz session {} with score {}",
        user.getEmail(),
        request.quizSessionId(),
        totalScore);

    return totalScore;
  }

  @Override
  public HistoryQuizSessionResponse getQuizSessionHistory(String quizSessionId, String userId) {
    QuizSession quizSession =
        quizSessionRepository
            .findById(quizSessionId)
            .orElseThrow(() -> new QuizException(ErrorCode.MESSAGE_INVALID_ID));

    if (quizSession.getStatus() != SessionStatus.COMPLETED) {
      throw new QuizException(ErrorCode.MESSAGE_INVALID_SESSION_STATUS);
    }

    if (quizSession.getParticipants().stream()
        .noneMatch(participant -> participant.getUserId().equals(userId))) {
      throw new QuizException(ErrorCode.MESSAGE_NOT_PARTICIPATED);
    }

    HistoryQuizSessionResponse response = new HistoryQuizSessionResponse();
    Quiz quiz =
        quizRepository
            .findById(quizSession.getQuizId())
            .orElseThrow(() -> new QuizException(ErrorCode.MESSAGE_INVALID_ID));

    MultipleChoiceQuiz multipleChoiceQuiz =
        multipleChoiceQuizRepository
            .findByQuizId(quizSession.getQuizId())
            .orElseThrow(() -> new QuizException(ErrorCode.MESSAGE_INVALID_ID));
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
        multipleChoiceQuizRepository
            .findByQuizId(quizSession.getQuizId())
            .orElseThrow(() -> new QuizException(ErrorCode.MESSAGE_INVALID_ID));
    int totalQuestions =
        multipleChoiceQuiz.getQuestions().size()
            + (!matchingQuiz.getMatchPairs().isEmpty() ? 1 : 0);
    return QuizSessionDetailResponse.builder()
        .id(quizSession.getId())
        .quizId(quizSession.getQuizId())
        .teacher(teacherResponse)
        .startTime(quizSession.getStartTime())
        .totalQuestions(totalQuestions)
        .status(quizSession.getStatus())
        .build();
  }

  @Override
  public void startQuizSession(String quizSessionId) {
    CustomUserDetail userDetail = SecurityUtils.getUserDetail();
    if (userDetail == null) {
      throw new QuizException(ErrorCode.MESSAGE_UNAUTHORIZED);
    }
    QuizSession quizSession =
        quizSessionRepository
            .findById(quizSessionId)
            .orElseThrow(() -> new QuizException(ErrorCode.MESSAGE_INVALID_ID));
    if (!quizSession.getTeacherId().equals(userDetail.getUser().getId())) {
      throw new QuizException(ErrorCode.MESSAGE_UNAUTHORIZED);
    }
    if (quizSession.getStatus() != SessionStatus.LOBBY) {
      throw new QuizException(ErrorCode.MESSAGE_INVALID_SESSION_STATUS);
    }
    quizSession.setStatus(SessionStatus.ACTIVE);
    quizSession.setStartTime(LocalDateTime.now());
    quizSessionRepository.save(quizSession);
    webSocketPublisher.publishStartExam(quizSessionId);
    log.info("Quiz session {} started by teacher {}", quizSessionId, userDetail.getUsername());
  }

  @Override
  public void closeQuizSession(String quizSessionId) {
    CustomUserDetail userDetail = SecurityUtils.getUserDetail();
    if (userDetail == null) {
      throw new QuizException(ErrorCode.MESSAGE_UNAUTHORIZED);
    }
    QuizSession quizSession =
        quizSessionRepository
            .findById(quizSessionId)
            .orElseThrow(() -> new QuizException(ErrorCode.MESSAGE_INVALID_ID));
    if (!quizSession.getTeacherId().equals(userDetail.getUser().getId())) {
      throw new QuizException(ErrorCode.MESSAGE_UNAUTHORIZED);
    }
    if (quizSession.getStatus() != SessionStatus.ACTIVE) {
      throw new QuizException(ErrorCode.MESSAGE_INVALID_SESSION_STATUS);
    }
    quizSession.setStatus(SessionStatus.COMPLETED);
    quizSession.setEndTime(LocalDateTime.now());
    quizSessionRepository.save(quizSession);
    webSocketPublisher.publishCloseQuizSession(quizSessionId);
    log.info("Quiz session {} closed by teacher {}", quizSessionId, userDetail.getUsername());
  }

  @Override
  public List<UserBaseResponse> getStudentsInQuizSession(String quizSessionId) {
    List<QuizSession.Participant> participants =
        quizSessionRepository
            .findById(quizSessionId)
            .orElseThrow(() -> new QuizException(ErrorCode.MESSAGE_INVALID_ID))
            .getParticipants();
    if (participants.isEmpty()) {
      return List.of();
    }

    List<String> studentIds =
        participants.stream().map(QuizSession.Participant::getUserId).toList();
    List<User> students = userRepository.findAllById(studentIds);
    return students.stream().map(userMapper::toUserBaseResponse).toList();
  }

  @Override
  public List<UserSubmitResponse> getScoreboard(String quizSessionId) {
    QuizSession quizSession =
        quizSessionRepository
            .findById(quizSessionId)
            .orElseThrow(() -> new QuizException(ErrorCode.MESSAGE_INVALID_ID));

    if (quizSession.getStatus() != SessionStatus.COMPLETED) {
      throw new QuizException(ErrorCode.MESSAGE_INVALID_SESSION_STATUS);
    }

    List<QuizSession.Participant> participants = quizSession.getParticipants();
    if (participants.isEmpty()) {
      log.info("No participants found for quiz session: {}", quizSessionId);
      return List.of();
    }

    // Get all user IDs and fetch users in a single database call
    List<String> userIds = participants.stream().map(QuizSession.Participant::getUserId).toList();
    List<User> usersList = userRepository.findAllById(userIds);

    // Create a map for efficient user lookup
    Map<String, User> usersMap =
        usersList.stream().collect(Collectors.toMap(User::getId, Function.identity()));

    // Create ranked scoreboard
    List<UserSubmitResponse> scoreboard = new ArrayList<>();
    int position = 1;

    // Sort participants by score (descending) before mapping to response objects
    List<QuizSession.Participant> sortedParticipants =
        participants.stream()
            .sorted(Comparator.comparing(p -> p.getScore() != null ? -p.getScore() : 0))
            .toList();

    for (QuizSession.Participant participant : sortedParticipants) {
      User user = usersMap.get(participant.getUserId());
      if (user == null) {
        log.warn("User not found for ID: {}", participant.getUserId());
        continue;
      }

      Integer score = participant.getScore() != null ? participant.getScore() : 0;

      UserSubmitResponse response =
          UserSubmitResponse.builder()
              .id(user.getId())
              .email(user.getEmail())
              .firstName(user.getFirstName())
              .lastName(user.getLastName())
              .quizSessionId(quizSessionId)
              .score(score)
              .rank(position++)
              .build();

      scoreboard.add(response);
    }

    log.info(
        "Generated scoreboard with {} participants for quiz session: {}",
        scoreboard.size(),
        quizSessionId);

    return scoreboard;
  }
}
