package com.tkt.quizedu.service.quiz;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.tkt.quizedu.data.collection.MultipleChoiceQuiz;
import com.tkt.quizedu.data.collection.Quiz;
import com.tkt.quizedu.data.dto.request.QuestionMultipleChoiceRequest;
import com.tkt.quizedu.data.dto.request.QuizCreationRequest;
import com.tkt.quizedu.data.dto.request.UpdateQuestionMultipleChoiceRequest;
import com.tkt.quizedu.data.dto.response.QuizCreationResponse;
import com.tkt.quizedu.data.repository.MultipleChoiceQuizRepository;
import com.tkt.quizedu.data.repository.QuizRepository;

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

  @Override
  public QuizCreationResponse save(QuizCreationRequest request) {
    Quiz quiz =
        Quiz.builder()
            .name(request.name())
            .description(request.description())
            .teacherId(request.teacherId())
            .subjectId(request.subjectId())
            .classIds(request.classIds())
            .isActive(request.isActive())
            .build();
    quiz = quizRepository.save(quiz);
    MultipleChoiceQuiz multipleChoiceQuiz = null;

    if (request.multipleChoiceQuiz() != null) {
      request.multipleChoiceQuiz().setQuizId(quiz.getId());

      //  Gán UUID cho từng câu hỏi nếu có
      if (request.multipleChoiceQuiz().getQuestions() != null) {
        request
            .multipleChoiceQuiz()
            .getQuestions()
            .forEach(
                q -> {
                  if (q.getQuestionId() == null) {
                    q.setQuestionId(UUID.randomUUID());
                  }
                });
      }

      multipleChoiceQuiz = multipleChoiceQuizRepository.save(request.multipleChoiceQuiz());
    }
    // còn các loại quiz khác
    return QuizCreationResponse.builder().quiz(quiz).multipleChoiceQuiz(multipleChoiceQuiz).build();
  }

  @Override
  public List<QuizCreationResponse> getAll() {
    List<Quiz> quizzes = quizRepository.findAll();
    return quizzes.stream()
        .map(
            quiz -> {
              MultipleChoiceQuiz multipleChoiceQuiz =
                  multipleChoiceQuizRepository.findByQuizId(quiz.getId());
              // còn các loại quiz khác
              return QuizCreationResponse.builder()
                  .quiz(quiz)
                  .multipleChoiceQuiz(multipleChoiceQuiz)
                  .build();
            })
        .toList();
  }

  @Override
  public QuizCreationResponse getById(String id) {
    Quiz quiz =
        quizRepository.findById(id).orElseThrow(() -> new RuntimeException("Quiz not found"));
    MultipleChoiceQuiz multipleChoiceQuiz = multipleChoiceQuizRepository.findByQuizId(quiz.getId());
    // còn các loại quiz khác
    return QuizCreationResponse.builder().quiz(quiz).multipleChoiceQuiz(multipleChoiceQuiz).build();
  }

  @Override
  public void delete(String id) {
    Quiz quiz =
        quizRepository.findById(id).orElseThrow(() -> new RuntimeException("Quiz not found"));
    if (multipleChoiceQuizRepository.findByQuizId(quiz.getId()) != null) {
      multipleChoiceQuizRepository.delete(multipleChoiceQuizRepository.findByQuizId(quiz.getId()));
    }
    quizRepository.delete(quiz);
  }

  @Override
  public QuizCreationResponse addMultipleChoiceQuizQuestion(
      String quizId, List<QuestionMultipleChoiceRequest> questions) {
    MultipleChoiceQuiz multipleChoiceQuiz = multipleChoiceQuizRepository.findByQuizId(quizId);
    Quiz quiz =
        quizRepository.findById(quizId).orElseThrow(() -> new RuntimeException("Quiz not found"));
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
    return QuizCreationResponse.builder().quiz(quiz).multipleChoiceQuiz(multipleChoiceQuiz).build();
  }

  @Override
  public void deleteMultipleChoiceQuizQuestion(String quizId, List<UUID> request) {
    Quiz quiz =
        quizRepository.findById(quizId).orElseThrow(() -> new RuntimeException("Quiz not found"));

    MultipleChoiceQuiz multipleChoiceQuiz = multipleChoiceQuizRepository.findByQuizId(quizId);
    if (multipleChoiceQuiz == null || multipleChoiceQuiz.getQuestions() == null) {
      throw new RuntimeException("Multiple choice quiz or questions not found");
    }

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
  public QuizCreationResponse updateMultipleChoiceQuizQuestion(
      String quizId, List<UpdateQuestionMultipleChoiceRequest> questions) {
    Quiz quiz =
        quizRepository.findById(quizId).orElseThrow(() -> new RuntimeException("Quiz not found"));

    MultipleChoiceQuiz multipleChoiceQuiz = multipleChoiceQuizRepository.findByQuizId(quizId);
    if (multipleChoiceQuiz == null) {
      throw new RuntimeException("Multiple choice quiz not found");
    }

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

    return QuizCreationResponse.builder().quiz(quiz).multipleChoiceQuiz(multipleChoiceQuiz).build();
  }
}
