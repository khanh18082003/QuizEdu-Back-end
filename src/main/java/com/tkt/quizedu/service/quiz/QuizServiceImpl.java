package com.tkt.quizedu.service.quiz;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tkt.quizedu.data.collection.CustomUserDetail;
import com.tkt.quizedu.data.collection.MatchingQuiz;
import com.tkt.quizedu.data.dto.request.*;
import com.tkt.quizedu.data.mapper.MatchingQuizMapper;
import com.tkt.quizedu.data.mapper.MultipleChoiceQuizMapper;
import com.tkt.quizedu.data.mapper.QuizMapper;
import com.tkt.quizedu.data.repository.MatchingQuizRepository;
import com.tkt.quizedu.utils.SecurityUtils;
import org.springframework.stereotype.Service;

import com.tkt.quizedu.data.collection.MultipleChoiceQuiz;
import com.tkt.quizedu.data.collection.Quiz;
import com.tkt.quizedu.data.dto.response.QuizResponse;
import com.tkt.quizedu.data.repository.MultipleChoiceQuizRepository;
import com.tkt.quizedu.data.repository.QuizRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

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

    @Override
    @Transactional
    public QuizResponse save(QuizCreationRequest request) {
        //Sua lai theo dang nhap cua giao vien
        CustomUserDetail userDetail = SecurityUtils.getUserDetail();
        Quiz quiz = quizMapper.toQuiz(request);
        quiz.setTeacherId(userDetail.getUser().getId());

        quiz = quizRepository.save(quiz);
        MultipleChoiceQuiz multipleChoiceQuiz = null;
        //  Gán UUID cho từng câu hỏi nếu có
        if (request.multipleChoiceQuiz() != null) {
            multipleChoiceQuiz = multipleChoiceQuizMapper.toMultipleChoiceQuiz(request.multipleChoiceQuiz());
            multipleChoiceQuiz.setQuizId(quiz.getId());
            multipleChoiceQuizRepository.save(multipleChoiceQuiz);
        }
        // Nếu có MatchingQuiz, map và lưu
        MatchingQuiz matchingQuiz = null;
        if (request.matchingQuiz() != null) {
            matchingQuiz = matchingQuizMapper.toMatchingQuiz(request.matchingQuiz());
            matchingQuiz.setQuizId(quiz.getId());
            // Gán lại danh sách MatchPair nếu bạn map từng MatchPair riêng
            List<MatchingQuiz.MatchPair> pairs = matchingQuizMapper.toMatchPairList(request.matchingQuiz().questions());
            matchingQuiz.setQuestions(pairs);
            matchingQuizRepository.save(matchingQuiz);
        }
        return QuizResponse.builder().quiz(quiz).multipleChoiceQuiz(multipleChoiceQuiz != null ? multipleChoiceQuizMapper.toMultipleChoiceQuizResponse(multipleChoiceQuiz) : null)
                .matchingQuiz(matchingQuiz != null ? matchingQuizMapper.toMatchingQuizResponse(matchingQuiz) : null).build();
    }

    @Override
    public List<QuizResponse> getAll() {
        CustomUserDetail userDetail = SecurityUtils.getUserDetail();
        List<Quiz> quizzes = quizRepository.findAllByTeacherId(userDetail.getUser().getId());
        return quizzes.stream()
                .map(
                        quiz -> {
                            MultipleChoiceQuiz multipleChoiceQuiz =
                                    multipleChoiceQuizRepository.findByQuizId(quiz.getId());
                            MatchingQuiz matchingQuiz =
                                    matchingQuizRepository.findByQuizId(quiz.getId());
                            // Nếu có các loại quiz khác, thêm logic lấy tương ứng
                            return QuizResponse.builder().quiz(quiz).multipleChoiceQuiz(multipleChoiceQuiz != null ? multipleChoiceQuizMapper.toMultipleChoiceQuizResponse(multipleChoiceQuiz) : null)
                                    .matchingQuiz(matchingQuiz != null ? matchingQuizMapper.toMatchingQuizResponse(matchingQuiz) : null).build();
                        })
                .toList();
    }

    @Override
    public QuizResponse getById(String id) {
        Quiz quiz =
                quizRepository.findById(id).orElseThrow(() -> new RuntimeException("Quiz not found"));
        MultipleChoiceQuiz multipleChoiceQuiz = multipleChoiceQuizRepository.findByQuizId(quiz.getId());
        MatchingQuiz matchingQuiz = matchingQuizRepository.findByQuizId(quiz.getId());
        // Nếu có các loại quiz khác, thêm logic lấy tương ứng
        return QuizResponse.builder().quiz(quiz).multipleChoiceQuiz(multipleChoiceQuiz != null ? multipleChoiceQuizMapper.toMultipleChoiceQuizResponse(multipleChoiceQuiz) : null)
                .matchingQuiz(matchingQuiz != null ? matchingQuizMapper.toMatchingQuizResponse(matchingQuiz) : null).build();
    }

    @Override
    public void delete(String id) {
        Quiz quiz =
                quizRepository.findById(id).orElseThrow(() -> new RuntimeException("Quiz not found"));
        if (multipleChoiceQuizRepository.findByQuizId(quiz.getId()) != null) {
            multipleChoiceQuizRepository.delete(multipleChoiceQuizRepository.findByQuizId(quiz.getId()));
        }
        if (matchingQuizRepository.findByQuizId(quiz.getId()) != null) {
            matchingQuizRepository.delete(matchingQuizRepository.findByQuizId(quiz.getId()));
        }
        //Nếu có các loại quiz khác, thêm logic xóa tương ứng
        quizRepository.delete(quiz);
    }

    @Override
    public QuizResponse addMultipleChoiceQuizQuestion(
            String quizId, List<QuestionMultipleChoiceRequest> questions) {

        Quiz quiz =
                quizRepository.findById(quizId).orElseThrow(() -> new RuntimeException("Quiz not found"));
        MultipleChoiceQuiz multipleChoiceQuiz = multipleChoiceQuizRepository.findByQuizId(quizId);
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
        return QuizResponse.builder().quiz(quiz).multipleChoiceQuiz(multipleChoiceQuizMapper.toMultipleChoiceQuizResponse(multipleChoiceQuiz))
                .build();
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
    public QuizResponse updateMultipleChoiceQuizQuestion(
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

        return QuizResponse.builder().quiz(quiz).multipleChoiceQuiz(multipleChoiceQuizMapper.toMultipleChoiceQuizResponse(multipleChoiceQuiz))
                .build();
    }

    @Override
    public QuizResponse addMatchingQuizQuestion(String quizId, List<MatchingQuestionRequest> questions) {
        Quiz quiz =
                quizRepository.findById(quizId).orElseThrow(() -> new RuntimeException("Quiz not found"));
        MatchingQuiz matchingQuiz = matchingQuizRepository.findByQuizId(quizId);
        List<MatchingQuiz.MatchPair> newPairs =
                questions.stream().map(
                        req -> {
                            MatchingQuiz.MatchPair pair = new MatchingQuiz.MatchPair();
                            pair.setId(UUID.randomUUID());
                            pair.setItemA(new MatchingQuiz.MatchItem(req.contentA(), req.typeA()));
                            pair.setItemB(new MatchingQuiz.MatchItem(req.contentB(), req.typeB()));
                            pair.setPoints(req.points());
                            return pair;
                        }
                ).toList();
        matchingQuiz.getQuestions().addAll(newPairs);
        matchingQuizRepository.save(matchingQuiz);
        return QuizResponse.builder().quiz(quiz).matchingQuiz(matchingQuizMapper.toMatchingQuizResponse(matchingQuiz))
                .build();
    }

    @Override
    public void deleteMatchingQuizQuestion(String quizId, List<UUID> request) {
        Quiz quiz =
                quizRepository.findById(quizId).orElseThrow(() -> new RuntimeException("Quiz not found"));
        MatchingQuiz matchingQuiz = matchingQuizRepository.findByQuizId(quizId);
        if (matchingQuiz == null || matchingQuiz.getQuestions() == null) {
            throw new RuntimeException("Matching quiz or questions not found");
        }
        int beforeSize = matchingQuiz.getQuestions().size();
        // Xoá các câu hỏi có ID nằm trong danh sách cần xoá
        matchingQuiz.setQuestions(
                matchingQuiz.getQuestions().stream()
                        .filter(q -> !request.contains(q.getId()))
                        .collect(Collectors.toList()));
        int afterSize = matchingQuiz.getQuestions().size();
        if (beforeSize == afterSize) {
            throw new RuntimeException("No matching question(s) found to delete.");
        }
        matchingQuizRepository.save(matchingQuiz);
    }

    @Override
    public QuizResponse updateMatchingQuizQuestion(String quizId, List<UpdateMatchingQuestionRequest> questions) {
        Quiz quiz =
                quizRepository.findById(quizId).orElseThrow(() -> new RuntimeException("Quiz not found"));

        MatchingQuiz matchingQuiz = matchingQuizRepository.findByQuizId(quizId);
        if (matchingQuiz == null) {
            throw new RuntimeException("Matching quiz not found");
        }

        List<MatchingQuiz.MatchPair> originalPairs = matchingQuiz.getQuestions();

        questions.forEach(
                req -> {
                    MatchingQuiz.MatchPair pair =
                            originalPairs.stream()
                                    .filter(q -> q.getId().equals(req.id()))
                                    .findFirst()
                                    .orElseThrow(
                                            () -> new RuntimeException("Pair not found: " + req.id()));

                    pair.setItemA(new MatchingQuiz.MatchItem(req.contentA(), req.typeA()));
                    pair.setItemB(new MatchingQuiz.MatchItem(req.contentB(), req.typeB()));
                    pair.setPoints(req.points());
                });

        matchingQuizRepository.save(matchingQuiz);
        return QuizResponse.builder().quiz(quiz).matchingQuiz(matchingQuizMapper.toMatchingQuizResponse(matchingQuiz))
                .build();
    }

    @Override
    public QuizResponse addQuizQuestion(String quizId, AddQuizRequest request) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new RuntimeException("Quiz not found"));

        MultipleChoiceQuiz multipleChoiceQuiz = null;
        MatchingQuiz matchingQuiz = null;

        switch (request.type()) {
            case MULTIPLE_CHOICE -> {
                if (multipleChoiceQuizRepository.existsByQuizId(quizId)) {
                    throw new RuntimeException("Multiple choice quiz already exists");
                }

                MultipleChoiceQuizRequest multipleChoiceQuizRequest =
                        objectMapper.convertValue(request.data(), MultipleChoiceQuizRequest.class);

                multipleChoiceQuiz = multipleChoiceQuizMapper.toMultipleChoiceQuiz(multipleChoiceQuizRequest);
                multipleChoiceQuiz.setQuizId(quizId);
                multipleChoiceQuiz = multipleChoiceQuizRepository.save(multipleChoiceQuiz);
            }

            case MATCHING -> {
                if (matchingQuizRepository.existsByQuizId(quizId)) {
                    throw new RuntimeException("Matching quiz already exists");
                }

                MatchingQuizRequest matchingQuizRequest =
                        objectMapper.convertValue(request.data(), MatchingQuizRequest.class);

                matchingQuiz = matchingQuizMapper.toMatchingQuiz(matchingQuizRequest);
                matchingQuiz.setQuizId(quizId);
                matchingQuiz.setQuestions(
                        matchingQuizMapper.toMatchPairList(matchingQuizRequest.questions())
                );
                matchingQuiz = matchingQuizRepository.save(matchingQuiz);
            }

            default -> throw new IllegalArgumentException("Unsupported quiz type: " + request.type());
        }

        return QuizResponse.builder()
                .quiz(quiz)
                .multipleChoiceQuiz(multipleChoiceQuizMapper.toMultipleChoiceQuizResponse(multipleChoiceQuizRepository.findByQuizId(quizId)))
                .matchingQuiz(matchingQuizMapper.toMatchingQuizResponse(matchingQuizRepository.findByQuizId(quizId)))
                .build();
    }
}
