package com.tkt.quizedu.service.quiz;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tkt.quizedu.data.collection.*;
import com.tkt.quizedu.data.constant.ErrorCode;
import com.tkt.quizedu.data.constant.MatchingType;
import com.tkt.quizedu.data.dto.request.*;
import com.tkt.quizedu.data.dto.response.*;
import com.tkt.quizedu.data.mapper.MatchingQuizMapper;
import com.tkt.quizedu.data.mapper.MultipleChoiceQuizMapper;
import com.tkt.quizedu.data.mapper.QuizMapper;
import com.tkt.quizedu.data.repository.MatchingQuizRepository;
import com.tkt.quizedu.data.repository.MultipleChoiceQuizRepository;
import com.tkt.quizedu.data.repository.QuizRepository;
import com.tkt.quizedu.data.repository.QuizSessionRepository;
import com.tkt.quizedu.exception.QuizException;
import com.tkt.quizedu.service.s3.IS3Service;
import com.tkt.quizedu.utils.SecurityUtils;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Slf4j(topic = "QUIZ-SERVICE")
public class QuizServiceImpl implements IQuizService {

  QuizRepository quizRepository;
  MultipleChoiceQuizRepository multipleChoiceQuizRepository;
  MatchingQuizRepository matchingQuizRepository;
  QuizMapper quizMapper;
  MultipleChoiceQuizMapper multipleChoiceQuizMapper;
  MatchingQuizMapper matchingQuizMapper;
  ObjectMapper objectMapper;
  IS3Service s3Service;
  QuizSessionRepository quizSessionRepository;

  @Override
  @Transactional
  public QuizResponse save(QuizCreationRequest request) {
    // Sua lai theo dang nhap cua giao vien
    CustomUserDetail userDetail = SecurityUtils.getUserDetail();
    Quiz quiz = quizMapper.toQuiz(request);
    quiz.setTeacherId(userDetail.getUser().getId());

    quiz = quizRepository.save(quiz);
    MultipleChoiceQuiz multipleChoiceQuiz = null;
    //  Gán UUID cho từng câu hỏi nếu có
    if (request.multipleChoiceQuiz() != null) {
      multipleChoiceQuiz =
          multipleChoiceQuizMapper.toMultipleChoiceQuiz(request.multipleChoiceQuiz());
      multipleChoiceQuiz.setQuizId(quiz.getId());
      multipleChoiceQuizRepository.save(multipleChoiceQuiz);
    }
    // Nếu có MatchingQuiz, map và lưu
    MatchingQuiz matchingQuiz = null;
    if (request.matchingQuiz() != null) {
      matchingQuiz = matchingQuizMapper.toMatchingQuiz(request.matchingQuiz());
      matchingQuiz.setQuizId(quiz.getId());
      // Gán lại danh sách MatchPair nếu bạn map từng MatchPair riêng
      List<MatchingQuiz.MatchPair> pairs =
          matchingQuizMapper.toMatchPairList(request.matchingQuiz().questions());
      matchingQuiz.setMatchPairs(pairs);
      matchingQuizRepository.save(matchingQuiz);
    }
    return QuizResponse.builder()
        .quiz(quiz)
        .multipleChoiceQuiz(
            multipleChoiceQuiz != null
                ? multipleChoiceQuizMapper.toMultipleChoiceQuizResponse(multipleChoiceQuiz)
                : null)
        .matchingQuiz(
            matchingQuiz != null ? matchingQuizMapper.toMatchingQuizResponse(matchingQuiz) : null)
        .build();
  }

  @Override
  public PaginationResponse<QuizResponse> getAll(int page, int pageSize) {
    CustomUserDetail userDetail = SecurityUtils.getUserDetail();
    String teacherId = userDetail.getUser().getId();

    Pageable pageable = PageRequest.of(page - 1, pageSize);

    // Lấy danh sách theo phân trang
    Page<Quiz> quizzesPage = quizRepository.findAllByTeacherId(teacherId, pageable);

    List<QuizResponse> quizResponse =
        quizzesPage.getContent().stream()
            .map(
                quiz -> {
                  MultipleChoiceQuiz multipleChoiceQuiz =
                      multipleChoiceQuizRepository.findByQuizId(quiz.getId()).orElse(null);
                  MatchingQuiz matchingQuiz = matchingQuizRepository.findByQuizId(quiz.getId());
                  return QuizResponse.builder()
                      .quiz(quiz)
                      .multipleChoiceQuiz(
                          multipleChoiceQuiz != null
                              ? multipleChoiceQuizMapper.toMultipleChoiceQuizResponse(
                                  multipleChoiceQuiz)
                              : null)
                      .matchingQuiz(
                          matchingQuiz != null
                              ? matchingQuizMapper.toMatchingQuizResponse(matchingQuiz)
                              : null)
                      .build();
                })
            .toList();

    return PaginationResponse.<QuizResponse>builder()
        .data(quizResponse)
        .page(page)
        .pageSize(pageSize)
        .pages(quizzesPage.getTotalPages())
        .total(quizzesPage.getTotalElements())
        .build();
  }

  @Override
  public QuizResponse getById(String id) {
    Quiz quiz =
        quizRepository.findById(id).orElseThrow(() -> new RuntimeException("Quiz not found"));
    MultipleChoiceQuiz multipleChoiceQuiz =
        multipleChoiceQuizRepository.findByQuizId(quiz.getId()).orElse(null);
    MatchingQuiz matchingQuiz = matchingQuizRepository.findByQuizId(quiz.getId());
    // Nếu có các loại quiz khác, thêm logic lấy tương ứng
    return QuizResponse.builder()
        .quiz(quiz)
        .multipleChoiceQuiz(
            multipleChoiceQuiz != null
                ? multipleChoiceQuizMapper.toMultipleChoiceQuizResponse(multipleChoiceQuiz)
                : null)
        .matchingQuiz(
            matchingQuiz != null ? matchingQuizMapper.toMatchingQuizResponse(matchingQuiz) : null)
        .build();
  }

  @Override
  public void delete(String id) {
    Quiz quiz =
        quizRepository.findById(id).orElseThrow(() -> new QuizException(ErrorCode.MESSAGE_INVALID_ID));
    if (!quiz.getClassIds().isEmpty()) {
      quiz.setActive(false);
      quizRepository.save(quiz);
      throw new QuizException(ErrorCode.MESSAGE_DELETE_QUIZ);
    }
    if (multipleChoiceQuizRepository.findByQuizId(quiz.getId()).isPresent()) {
      multipleChoiceQuizRepository.delete(
              Objects.requireNonNull(multipleChoiceQuizRepository.findByQuizId(quiz.getId()).orElse(null)));
    }
    if (matchingQuizRepository.findByQuizId(quiz.getId()) != null) {
      matchingQuizRepository.delete(matchingQuizRepository.findByQuizId(quiz.getId()));
    }
    // Nếu có các loại quiz khác, thêm logic xóa tương ứng
    quizRepository.delete(quiz);
  }

  @Override
  public QuizResponse addMultipleChoiceQuizQuestion(
      String quizId, List<QuestionMultipleChoiceRequest> questions) {

    Quiz quiz =
        quizRepository.findById(quizId).orElseThrow(() -> new RuntimeException("Quiz not found"));
    MultipleChoiceQuiz multipleChoiceQuiz =
        multipleChoiceQuizRepository.findByQuizId(quizId).orElse(null);
    List<MultipleChoiceQuiz.Question> newQuestions =
        questions.stream()
            .map(
                req -> {
                  MultipleChoiceQuiz.Question q = new MultipleChoiceQuiz.Question();
                  q.setQuestionId(UUID.randomUUID());
                  q.setQuestionText(req.questionText());
                  q.setHint(req.hint());
                  q.setTimeLimit(req.timeLimit());
                  q.setPoints(req.points());
                  q.setAllowMultipleAnswers(req.allowMultipleAnswers());
                  q.setAnswers(req.answers());
                  return q;
                })
            .toList();
    multipleChoiceQuiz.getQuestions().addAll(newQuestions);
    multipleChoiceQuizRepository.save(multipleChoiceQuiz);
    return QuizResponse.builder()
        .quiz(quiz)
        .multipleChoiceQuiz(
            multipleChoiceQuizMapper.toMultipleChoiceQuizResponse(multipleChoiceQuiz))
        .build();
  }

  @Override
  public void deleteMultipleChoiceQuizQuestion(String quizId, List<UUID> request) {
    MultipleChoiceQuiz multipleChoiceQuiz =
        multipleChoiceQuizRepository.findByQuizId(quizId).orElse(null);

    int beforeSize = multipleChoiceQuiz.getQuestions().size();

    // Xoá các câu hỏi có ID nằm trong danh sách cần xoá
    multipleChoiceQuiz.setQuestions(
        multipleChoiceQuiz.getQuestions().stream()
            .filter(q -> !request.contains(q.getQuestionId()))
            .collect(Collectors.toList()));

    int afterSize = multipleChoiceQuiz.getQuestions().size();

    if (beforeSize == afterSize) {
      throw new RuntimeException("No matching question(s) found to delete.");
    }

    multipleChoiceQuizRepository.save(multipleChoiceQuiz);
  }

  @Override
  public QuizResponse updateMultipleChoiceQuizQuestion(
      String quizId, List<UpdateQuestionMultipleChoiceRequest> questions) {
    Quiz quiz =
        quizRepository.findById(quizId).orElseThrow(() -> new RuntimeException("Quiz not found"));

    MultipleChoiceQuiz multipleChoiceQuiz =
        multipleChoiceQuizRepository.findByQuizId(quizId).orElse(null);

    List<MultipleChoiceQuiz.Question> originalQuestions = multipleChoiceQuiz.getQuestions();

    questions.forEach(
        req -> {
          MultipleChoiceQuiz.Question question =
              originalQuestions.stream()
                  .filter(q -> q.getQuestionId().equals(req.questionId()))
                  .findFirst()
                  .orElseThrow(
                      () -> new RuntimeException("Question not found: " + req.questionId()));

          question.setQuestionText(req.questionText());
          question.setHint(req.hint());
          question.setTimeLimit(req.timeLimit());
          question.setPoints(req.points());
          question.setAllowMultipleAnswers(req.allowMultipleAnswers());
          question.setAnswers(req.answers());
        });

    multipleChoiceQuizRepository.save(multipleChoiceQuiz);

    return QuizResponse.builder()
        .quiz(quiz)
        .multipleChoiceQuiz(
            multipleChoiceQuizMapper.toMultipleChoiceQuizResponse(multipleChoiceQuiz))
        .build();
  }

  @Override
  public int evaluateMultipleChoiceQuizQuestion(
      String quizSessionId, List<SubmitMultipleChoiceRequest> requestList) {
    if (requestList == null || requestList.isEmpty()) {
      return 0;
    }
    QuizSession quizSession =
        quizSessionRepository
            .findById(quizSessionId)
            .orElseThrow(() -> new QuizException(ErrorCode.MESSAGE_INVALID_ID));
    String quizId = quizSession.getQuizId();
    MultipleChoiceQuiz multipleChoiceQuiz =
        multipleChoiceQuizRepository.findByQuizId(quizId).orElse(null);

    List<MultipleChoiceQuiz.Question> questions = multipleChoiceQuiz.getQuestions();
    if (questions == null || questions.isEmpty()) {
      throw new RuntimeException("No questions found in the multiple choice quiz");
    }

    int totalScore = 0;

    for (SubmitMultipleChoiceRequest submitRequest : requestList) {
      MultipleChoiceQuiz.Question question =
          questions.stream()
              .filter(q -> q.getQuestionId().toString().equals(submitRequest.questionId()))
              .findFirst()
              .orElseThrow(
                  () -> new RuntimeException("Question not found: " + submitRequest.questionId()));

      List<MultipleChoiceQuiz.Question.AnswerParticipant> evaluatedParticipants =
          submitRequest.answerParticipant().stream()
              .map(
                  participant -> {
                    boolean isCorrect =
                        question.getAnswers().stream()
                            .anyMatch(
                                answer ->
                                    answer.getAnswerText().equals(participant.getAnswer())
                                        && answer.isCorrect());

                    MultipleChoiceQuiz.Question.AnswerParticipant evaluated =
                        new MultipleChoiceQuiz.Question.AnswerParticipant();
                    evaluated.setUserId(SecurityUtils.getUserDetail().getUser().getId());
                    evaluated.setQuizSessionId(quizSessionId);
                    evaluated.setAnswer(participant.getAnswer());
                    evaluated.setCorrect(isCorrect);
                    return evaluated;
                  })
              .toList();

      // Gắn danh sách kết quả đã đánh giá vào câu hỏi
      question.setAnswerParticipants(evaluatedParticipants);

      // Nếu người dùng chọn đúng tất cả đáp án đúng (và không chọn sai), tính điểm
      List<String> correctAnswers =
          question.getAnswers().stream()
              .filter(MultipleChoiceQuiz.Question.AnswerOption::isCorrect)
              .map(MultipleChoiceQuiz.Question.AnswerOption::getAnswerText)
              .toList();

      List<String> userAnswers =
          evaluatedParticipants.stream()
              .filter(MultipleChoiceQuiz.Question.AnswerParticipant::isCorrect)
              .map(MultipleChoiceQuiz.Question.AnswerParticipant::getAnswer)
              .toList();

      boolean fullCorrect =
          correctAnswers.size() == userAnswers.size() && correctAnswers.containsAll(userAnswers);

      if (fullCorrect) {
        totalScore += question.getPoints();
      }
    }

    multipleChoiceQuizRepository.save(multipleChoiceQuiz);
    return totalScore;
  }

  @Override
  public List<MultipleChoiceQuiz.Question> getMultipleChoiceHistoryByUserId(
      String quizSessionId, String userId) {

    QuizSession quizSession =
        quizSessionRepository
            .findById(quizSessionId)
            .orElseThrow(() -> new RuntimeException("Quiz session not found"));
    String quizId = quizSession.getQuizId();
    MultipleChoiceQuiz multipleChoiceQuiz =
        multipleChoiceQuizRepository.findByQuizId(quizId).orElse(null);

    List<MultipleChoiceQuiz.Question> questions = multipleChoiceQuiz.getQuestions();
    if (questions == null || questions.isEmpty()) {
      throw new RuntimeException("No questions found in the multiple choice quiz");
    }

    // Lọc các câu hỏi mà user có tham gia trả lời
    return questions.stream()
        .filter(
            q ->
                q.getAnswerParticipants() != null
                    && q.getAnswerParticipants().stream()
                        .anyMatch(
                            ap ->
                                userId.equals(ap.getUserId())
                                    && quizSessionId.equals(ap.getQuizSessionId())))
        .collect(Collectors.toList());
  }

  @Override
  public int evaluateMatchingQuizQuestion(
      String quizSessionId, List<MatchingQuiz.AnswerPair> request) {
    if (request == null || request.isEmpty()) {
      return 0;
    }
    QuizSession quizSession =
        quizSessionRepository
            .findById(quizSessionId)
            .orElseThrow(() -> new RuntimeException("Quiz session not found"));
    String quizId = quizSession.getQuizId();
    MatchingQuiz matchingQuiz = matchingQuizRepository.findByQuizId(quizId);
    if (matchingQuiz == null) {
      throw new RuntimeException("Matching quiz not found for quizId: " + quizId);
    }

    List<MatchingQuiz.MatchPair> matchPairs = matchingQuiz.getMatchPairs();
    if (matchPairs == null || matchPairs.isEmpty()) {
      throw new RuntimeException("No questions found in the matching quiz");
    }

    List<MatchingQuiz.AnswerPair> evaluatedAnswers = new ArrayList<>();
    int totalScore = 0;

    for (MatchingQuiz.AnswerPair submittedAnswer : request) {
      boolean isCorrect = false;
      int points = 0;

      for (MatchingQuiz.MatchPair pair : matchPairs) {
        if (pair.getId().equals(submittedAnswer.getMatchPairId())) {
          if ((pair.getItemA().getContent().equals(submittedAnswer.getItemA().getContent())
                  && pair.getItemB().getContent().equals(submittedAnswer.getItemB().getContent()))
              || (pair.getItemA().getContent().equals(submittedAnswer.getItemB().getContent())
                  && pair.getItemB()
                      .getContent()
                      .equals(submittedAnswer.getItemA().getContent()))) {
            isCorrect = true;
            points = pair.getPoints(); // cộng điểm nếu đúng
          }
          break;
        }
      }

      MatchingQuiz.AnswerPair evaluatedAnswer = new MatchingQuiz.AnswerPair();
      evaluatedAnswer.setMatchPairId(submittedAnswer.getMatchPairId());
      evaluatedAnswer.setItemA(submittedAnswer.getItemA());
      evaluatedAnswer.setItemB(submittedAnswer.getItemB());
      evaluatedAnswer.setCorrect(isCorrect);

      if (isCorrect) {
        totalScore += points;
      }

      evaluatedAnswers.add(evaluatedAnswer);
    }

    MatchingQuiz.UserAnswer userAnswer =
        MatchingQuiz.UserAnswer.builder()
            .userId(SecurityUtils.getUserDetail().getUser().getId())
            .answers(evaluatedAnswers)
            .quizSessionId(quizSessionId)
            .build();

    matchingQuiz.getAnswerParticipants().add(userAnswer);
    matchingQuizRepository.save(matchingQuiz);

    return totalScore;
  }

  @Override
  public List<MatchingQuiz.UserAnswer> getMatchingHistoryByUserId(
      String quizSessionId, String userId) {
    QuizSession quizSession =
        quizSessionRepository
            .findById(quizSessionId)
            .orElseThrow(() -> new RuntimeException("Quiz session not found"));
    String quizId = quizSession.getQuizId();
    MatchingQuiz matchingQuiz = matchingQuizRepository.findByQuizId(quizId);
    if (matchingQuiz == null) {
      throw new RuntimeException("Matching quiz not found for quizId: " + quizId);
    }
    List<MatchingQuiz.UserAnswer> userAnswers = matchingQuiz.getAnswerParticipants();
    if (userAnswers == null || userAnswers.isEmpty()) {
      throw new RuntimeException("No answers found in the matching quiz");
    }
    // Lọc các câu trả lời của user theo quizSessionId và userId
    return userAnswers.stream()
        .filter(ua -> ua.getUserId().equals(userId) && ua.getQuizSessionId().equals(quizSessionId))
        .collect(Collectors.toList());
  }

  @Override
  public QuizResponse addMatchingQuizQuestion(
      String quizId, List<MatchingQuestionRequest> questions) {
    Quiz quiz =
        quizRepository.findById(quizId).orElseThrow(() -> new RuntimeException("Quiz not found"));
    MatchingQuiz matchingQuiz = matchingQuizRepository.findByQuizId(quizId);
    List<MatchingQuiz.MatchPair> newPairs =
        questions.stream()
            .map(
                req -> {
                  MatchingQuiz.MatchPair pair = new MatchingQuiz.MatchPair();
                  pair.setId(UUID.randomUUID());
                  if (req.getFileContentA() != null) {
                    String contentA = s3Service.uploadFile(req.getFileContentA());
                    pair.setItemA(new MatchingQuiz.MatchItem(contentA, req.getTypeA()));
                  } else {
                    pair.setItemA(new MatchingQuiz.MatchItem(req.getContentA(), req.getTypeA()));
                  }
                  if (req.getFileContentB() != null) {
                    String contentB = s3Service.uploadFile(req.getFileContentB());
                    pair.setItemB(new MatchingQuiz.MatchItem(contentB, req.getTypeB()));
                  } else {
                    pair.setItemB(new MatchingQuiz.MatchItem(req.getContentB(), req.getTypeB()));
                  }
                  pair.setPoints(req.getPoints());
                  return pair;
                })
            .toList();
    matchingQuiz.getMatchPairs().addAll(newPairs);
    matchingQuizRepository.save(matchingQuiz);
    return QuizResponse.builder()
        .quiz(quiz)
        .matchingQuiz(matchingQuizMapper.toMatchingQuizResponse(matchingQuiz))
        .build();
  }

  @Override
  public void deleteMatchingQuizQuestion(String quizId, List<UUID> request) {
    Quiz quiz =
        quizRepository.findById(quizId).orElseThrow(() -> new RuntimeException("Quiz not found"));
    MatchingQuiz matchingQuiz = matchingQuizRepository.findByQuizId(quizId);
    if (matchingQuiz == null || matchingQuiz.getMatchPairs() == null) {
      throw new RuntimeException("Matching quiz or questions not found");
    }
    int beforeSize = matchingQuiz.getMatchPairs().size();
    // Xoá các câu hỏi có ID nằm trong danh sách cần xoá
    matchingQuiz.setMatchPairs(
        matchingQuiz.getMatchPairs().stream()
            .filter(q -> !request.contains(q.getId()))
            .collect(Collectors.toList()));
    int afterSize = matchingQuiz.getMatchPairs().size();
    if (beforeSize == afterSize) {
      throw new RuntimeException("No matching question(s) found to delete.");
    }
    matchingQuizRepository.save(matchingQuiz);
  }

  @Override
  @Transactional
  public QuizResponse updateMatchingQuizQuestion(
      String quizId, UpdateMatchingQuestionRequest request) {

    Quiz quiz =
        quizRepository
            .findById(quizId)
            .orElseThrow(() -> new QuizException(ErrorCode.MESSAGE_INVALID_ID));

    MatchingQuiz matchingQuiz = matchingQuizRepository.findByQuizId(quizId);
    if (matchingQuiz == null) {
      throw new QuizException(ErrorCode.MESSAGE_INVALID_ID);
    }

    // Cập nhật timeLimit nếu có
    if (request.getTimeLimit() != null) {
      matchingQuiz.setTimeLimit(request.getTimeLimit());
    }

    List<MatchingQuiz.MatchPair> originalPairs = matchingQuiz.getMatchPairs();
    if (request.getQuestions() == null) {
      matchingQuizRepository.save(matchingQuiz);
      return QuizResponse.builder()
          .quiz(quiz)
          .matchingQuiz(matchingQuizMapper.toMatchingQuizResponse(matchingQuiz))
          .build();
    }

    for (UpdateMatchingQuestion req : request.getQuestions()) {

      MatchingQuiz.MatchPair pair =
          originalPairs.stream()
              .filter(q -> q.getId().equals(req.getId()))
              .findFirst()
              .orElseThrow(() -> new QuizException(ErrorCode.MESSAGE_INVALID_ID));

      // ===== Xử lý itemA =====
      MatchingType typeA =
          req.getTypeA() != null ? req.getTypeA() : pair.getItemA().getMatchingType();
      String contentA;
      if (typeA == MatchingType.IMAGE && req.getFileContentA() != null) {
        contentA = s3Service.uploadFile(req.getFileContentA());
      } else if (req.getTextContentA() != null) {
        contentA = req.getTextContentA();
      } else {
        contentA = pair.getItemA().getContent();
      }

      // ===== Xử lý itemB =====
      MatchingType typeB =
          req.getTypeB() != null ? req.getTypeB() : pair.getItemB().getMatchingType();
      String contentB;
      if (typeB == MatchingType.IMAGE && req.getFileContentB() != null) {
        contentB = s3Service.uploadFile(req.getFileContentB());
      } else if (req.getTextContentB() != null) {
        contentB = req.getTextContentB();
      } else {
        contentB = pair.getItemB().getContent();
      }

      // ===== Set lại itemA, itemB, points =====
      pair.setItemA(new MatchingQuiz.MatchItem(contentA, typeA));
      pair.setItemB(new MatchingQuiz.MatchItem(contentB, typeB));
      if (req.getPoints() != null) {
        pair.setPoints(req.getPoints());
      }
    }

    matchingQuizRepository.save(matchingQuiz);

    return QuizResponse.builder()
        .quiz(quiz)
        .matchingQuiz(matchingQuizMapper.toMatchingQuizResponse(matchingQuiz))
        .build();
  }

  @Override
  public QuizResponse addQuizQuestion(String quizId, AddQuizRequest request) {
    Quiz quiz =
        quizRepository.findById(quizId).orElseThrow(() -> new RuntimeException("Quiz not found"));

    MultipleChoiceQuiz multipleChoiceQuiz = null;
    MatchingQuiz matchingQuiz = null;

    switch (request.type()) {
      case MULTIPLE_CHOICE -> {
        if (multipleChoiceQuizRepository.existsByQuizId(quizId)) {
          throw new RuntimeException("Multiple choice quiz already exists");
        }

        MultipleChoiceQuizRequest multipleChoiceQuizRequest =
            objectMapper.convertValue(request.data(), MultipleChoiceQuizRequest.class);

        multipleChoiceQuiz =
            multipleChoiceQuizMapper.toMultipleChoiceQuiz(multipleChoiceQuizRequest);
        multipleChoiceQuiz.setQuizId(quizId);
        multipleChoiceQuizRepository.save(multipleChoiceQuiz);
      }

      case MATCHING -> {
        if (matchingQuizRepository.existsByQuizId(quizId)) {
          throw new RuntimeException("Matching quiz already exists");
        }

        MatchingQuizRequest matchingQuizRequest =
            objectMapper.convertValue(request.data(), MatchingQuizRequest.class);

        matchingQuiz = matchingQuizMapper.toMatchingQuiz(matchingQuizRequest);
        matchingQuiz.setQuizId(quizId);
        matchingQuiz.setMatchPairs(
            matchingQuizMapper.toMatchPairList(matchingQuizRequest.questions()));
        matchingQuizRepository.save(matchingQuiz);
      }

      default -> throw new IllegalArgumentException("Unsupported quiz type: " + request.type());
    }

    return QuizResponse.builder()
        .quiz(quiz)
        .multipleChoiceQuiz(
            multipleChoiceQuizMapper.toMultipleChoiceQuizResponse(
                multipleChoiceQuizRepository.findByQuizId(quizId).orElse(null)))
        .matchingQuiz(
            matchingQuizMapper.toMatchingQuizResponse(matchingQuizRepository.findByQuizId(quizId)))
        .build();
  }

  @Override
  public PracticeResponse getQuizPractice(PracticeRequest request) {
    MultipleChoiceQuizResponse multipleChoiceQuiz = null;
    List<MultipleChoiceQuizResponse.QuestionResponse> questions = new ArrayList<>();
    MatchingQuizResponse matchingQuiz = null;
    List<MatchingQuizResponse.MatchPairResponse> matchPairs = new ArrayList<>();

    for (String quizId : request.quizIDs()) {
      MultipleChoiceQuiz mcQuiz = multipleChoiceQuizRepository.findByQuizId(quizId).orElse(null);
      if (mcQuiz != null) {
        multipleChoiceQuiz = multipleChoiceQuizMapper.toMultipleChoiceQuizResponse(mcQuiz);
        questions.addAll(multipleChoiceQuiz.getQuestions());
      }
      MatchingQuiz mQuiz = matchingQuizRepository.findByQuizId(quizId);
      if (mQuiz != null) {
        matchingQuiz = matchingQuizMapper.toMatchingQuizResponse(mQuiz);
        matchPairs.addAll(matchingQuiz.getQuestions());
      }
    }
    // Xáo trộn câu hỏi và cặp ghép
    questions.sort((q1, q2) -> Double.compare(Math.random(), Math.random()));
    matchPairs.sort((p1, p2) -> Double.compare(Math.random(), Math.random()));

    if (questions.size() > request.quantityMultipleChoice()) {
      questions = questions.subList(0, request.quantityMultipleChoice());
    }
    if (matchPairs.size() > request.quantityMultipleChoice()) {
      matchPairs = matchPairs.subList(0, request.quantityMultipleChoice());
    }
    assert multipleChoiceQuiz != null;
    multipleChoiceQuiz.setQuestions(questions);
    assert matchingQuiz != null;
    matchingQuiz.setQuestions(matchPairs);
    return PracticeResponse.builder()
        .multipleChoiceQuiz(multipleChoiceQuiz)
        .matchingQuiz(matchingQuiz)
        .build();
  }

  @Override
  public void updateQuiz(String quizId, UpdateQuizRequest request) {
    Quiz quiz =
        quizRepository.findById(quizId).orElseThrow(() -> new QuizException(ErrorCode.MESSAGE_INVALID_ID));
    if (request.name() != null) {
      quiz.setName(request.name());
    }
    if (request.description() != null) {
      quiz.setDescription(request.description());
    }
    if (request.isActive() != quiz.isActive()) {
        quiz.setActive(request.isActive());
    }
    if (request.isPublic() != quiz.isPublic()) {
        quiz.setPublic(request.isPublic());
    }
    quizRepository.save(quiz);
  }
  
  @Override
  public QuestionsOfQuizResponse getAllQuestionsByQuizId(String id) {
    Quiz quiz =
        quizRepository
            .findById(id)
            .orElseThrow(() -> new QuizException(ErrorCode.MESSAGE_INVALID_ID));
    MultipleChoiceQuiz multipleChoiceQuiz =
        multipleChoiceQuizRepository.findByQuizId(quiz.getId()).orElse(null);
    if (multipleChoiceQuiz != null) {
      List<MultipleChoiceQuiz.Question> questions = multipleChoiceQuiz.getQuestions();
      if (questions != null && !questions.isEmpty()) {
        // Gán UUID cho từng câu hỏi nếu chưa có
        questions.forEach(
            question -> {
              if (question.getAnswers() != null) {
                Collections.shuffle(question.getAnswers());
              }
            });
        multipleChoiceQuiz.setQuestions(questions);
      }
      Collections.shuffle(multipleChoiceQuiz.getQuestions());
    }
    MatchingQuiz matchingQuiz = matchingQuizRepository.findByQuizId(quiz.getId());
    MatchingQuizDetailResponse matchingQuizResponse =
        MatchingQuizDetailResponse.builder()
            .id(matchingQuiz.getId())
            .quizId(matchingQuiz.getQuizId())
            .timeLimit(matchingQuiz.getTimeLimit())
            .itemA(
                new ArrayList<>(
                    matchingQuiz.getMatchPairs().stream()
                        .map(
                            pair ->
                                MatchingQuizDetailResponse.MatchItemResponse.builder()
                                    .id(pair.getId())
                                    .content(pair.getItemA().getContent())
                                    .matchingType(pair.getItemA().getMatchingType())
                                    .build())
                        .toList()))
            .itemB(
                new ArrayList<>(
                    matchingQuiz.getMatchPairs().stream()
                        .map(
                            pair ->
                                MatchingQuizDetailResponse.MatchItemResponse.builder()
                                    .content(pair.getItemB().getContent())
                                    .matchingType(pair.getItemB().getMatchingType())
                                    .build())
                        .toList()))
            .build();
    Collections.shuffle(matchingQuizResponse.getItemA());
    Collections.shuffle(matchingQuizResponse.getItemB());
    // Nếu có các loại quiz khác, thêm logic lấy tương ứng
    return QuestionsOfQuizResponse.builder()
        .quiz(quizMapper.toQuizBaseResponse(quiz))
        .multipleChoiceQuiz(
            multipleChoiceQuiz != null
                ? multipleChoiceQuizMapper.toMultipleChoiceV2Response(multipleChoiceQuiz)
                : null)
        .matchingQuiz(matchingQuizResponse)
        .build();
  }
}
