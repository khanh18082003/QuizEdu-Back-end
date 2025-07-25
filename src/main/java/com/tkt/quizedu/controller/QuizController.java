package com.tkt.quizedu.controller;

import java.util.List;
import java.util.UUID;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.tkt.quizedu.component.Translator;
import com.tkt.quizedu.data.constant.EndpointConstant;
import com.tkt.quizedu.data.constant.ErrorCode;
import com.tkt.quizedu.data.dto.request.*;
import com.tkt.quizedu.data.dto.response.PaginationResponse;
import com.tkt.quizedu.data.dto.response.QuizResponse;
import com.tkt.quizedu.data.dto.response.SuccessApiResponse;
import com.tkt.quizedu.service.quiz.IQuizService;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping(EndpointConstant.ENDPOINT_QUIZ)
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Slf4j(topic = "Quiz-CONTROLLER")
@Tag(name = "Quiz Management", description = "APIs for managing game-related operations")
public class QuizController {
  IQuizService quizService;

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
  SuccessApiResponse<QuizResponse> save(@RequestBody @Valid QuizCreationRequest req) {
    return SuccessApiResponse.<QuizResponse>builder()
        .code(ErrorCode.MESSAGE_SUCCESS.getCode())
        .status(HttpStatus.CREATED.value())
        .message(Translator.toLocale(ErrorCode.MESSAGE_SUCCESS.getCode()))
        .data(quizService.save(req))
        .build();
  }

  @GetMapping
  @ResponseStatus(HttpStatus.OK)
  SuccessApiResponse<PaginationResponse<QuizResponse>> getAll(
      @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "9") int pageSize) {
    return SuccessApiResponse.<PaginationResponse<QuizResponse>>builder()
        .code(ErrorCode.MESSAGE_SUCCESS.getCode())
        .status(HttpStatus.OK.value())
        .message(Translator.toLocale(ErrorCode.MESSAGE_SUCCESS.getCode()))
        .data(quizService.getAll(page, pageSize))
        .build();
  }

  @GetMapping("/{id}")
  @ResponseStatus(HttpStatus.OK)
  SuccessApiResponse<QuizResponse> getById(@PathVariable String id) {
    return SuccessApiResponse.<QuizResponse>builder()
        .code(ErrorCode.MESSAGE_SUCCESS.getCode())
        .status(HttpStatus.OK.value())
        .message(Translator.toLocale(ErrorCode.MESSAGE_SUCCESS.getCode()))
        .data(quizService.getById(id))
        .build();
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.OK)
  SuccessApiResponse<Void> deleteById(@PathVariable String id) {
    quizService.delete(id);
    return SuccessApiResponse.<Void>builder()
        .code(ErrorCode.MESSAGE_SUCCESS.getCode())
        .status(HttpStatus.OK.value())
        .message(Translator.toLocale(ErrorCode.MESSAGE_SUCCESS.getCode()))
        .build();
  }

  @DeleteMapping("/multiple-choice-quizzes/{quizId}/questions")
  @ResponseStatus(HttpStatus.OK)
  SuccessApiResponse<Void> DeleteMultipleChoiceQuizQuestion(
      @PathVariable String quizId, @RequestBody @Valid List<UUID> request) {
    quizService.deleteMultipleChoiceQuizQuestion(quizId, request);
    return SuccessApiResponse.<Void>builder()
        .code(ErrorCode.MESSAGE_SUCCESS.getCode())
        .status(HttpStatus.OK.value())
        .message(Translator.toLocale(ErrorCode.MESSAGE_SUCCESS.getCode()))
        .build();
  }

  @PostMapping("/multiple-choice-quizzes/{quizId}/questions")
  @ResponseStatus(HttpStatus.OK)
  SuccessApiResponse<QuizResponse> addMultipleChoiceQuizQuestion(
      @PathVariable String quizId,
      @RequestBody @Valid List<QuestionMultipleChoiceRequest> questions) {
    QuizResponse response = quizService.addMultipleChoiceQuizQuestion(quizId, questions);
    return SuccessApiResponse.<QuizResponse>builder()
        .code(ErrorCode.MESSAGE_SUCCESS.getCode())
        .status(HttpStatus.OK.value())
        .message(Translator.toLocale(ErrorCode.MESSAGE_SUCCESS.getCode()))
        .data(response)
        .build();
  }

  @PutMapping("/multiple-choice-quizzes/{quizId}/questions")
  @ResponseStatus(HttpStatus.OK)
  SuccessApiResponse<QuizResponse> updateMultipleChoiceQuizQuestion(
      @PathVariable String quizId,
      @RequestBody @Valid List<UpdateQuestionMultipleChoiceRequest> questions) {
    // Assuming the service method is implemented to handle updates
    QuizResponse response = quizService.updateMultipleChoiceQuizQuestion(quizId, questions);
    return SuccessApiResponse.<QuizResponse>builder()
        .code(ErrorCode.MESSAGE_SUCCESS.getCode())
        .status(HttpStatus.OK.value())
        .message(Translator.toLocale(ErrorCode.MESSAGE_SUCCESS.getCode()))
        .data(response)
        .build();
  }

  // Additional endpoints for matching quizzes, etc. can be added here
  @PostMapping("/matching-quizzes/{quizId}/questions")
  @ResponseStatus(HttpStatus.OK)
  SuccessApiResponse<QuizResponse> addMatchingQuizQuestion(
      @PathVariable String quizId, @RequestBody @Valid List<MatchingQuestionRequest> questions) {
    QuizResponse response = quizService.addMatchingQuizQuestion(quizId, questions);
    return SuccessApiResponse.<QuizResponse>builder()
        .code(ErrorCode.MESSAGE_SUCCESS.getCode())
        .status(HttpStatus.OK.value())
        .message(Translator.toLocale(ErrorCode.MESSAGE_SUCCESS.getCode()))
        .data(response)
        .build();
  }

  @DeleteMapping("/matching-quizzes/{quizId}/questions")
  @ResponseStatus(HttpStatus.OK)
  SuccessApiResponse<Void> deleteMatchingQuizQuestion(
      @PathVariable String quizId, @RequestBody @Valid List<UUID> request) {
    quizService.deleteMatchingQuizQuestion(quizId, request);
    return SuccessApiResponse.<Void>builder()
        .code(ErrorCode.MESSAGE_SUCCESS.getCode())
        .status(HttpStatus.OK.value())
        .message(Translator.toLocale(ErrorCode.MESSAGE_SUCCESS.getCode()))
        .build();
  }

  @PutMapping("/matching-quizzes/{quizId}/questions")
  @ResponseStatus(HttpStatus.OK)
  SuccessApiResponse<QuizResponse> updateMatchingQuizQuestion(
      @PathVariable String quizId,
      @RequestBody @Valid List<UpdateMatchingQuestionRequest> questions) {
    QuizResponse response = quizService.updateMatchingQuizQuestion(quizId, questions);
    return SuccessApiResponse.<QuizResponse>builder()
        .code(ErrorCode.MESSAGE_SUCCESS.getCode())
        .status(HttpStatus.OK.value())
        .message(Translator.toLocale(ErrorCode.MESSAGE_SUCCESS.getCode()))
        .data(response)
        .build();
  }

  // add quiz type if needed
  @PostMapping("/{quizId}/add")
  @ResponseStatus(HttpStatus.OK)
  SuccessApiResponse<QuizResponse> addQuizQuestion(
      @PathVariable String quizId, @RequestBody @Valid AddQuizRequest request) {
    QuizResponse response = quizService.addQuizQuestion(quizId, request);
    return SuccessApiResponse.<QuizResponse>builder()
        .code(ErrorCode.MESSAGE_SUCCESS.getCode())
        .status(HttpStatus.OK.value())
        .message(Translator.toLocale(ErrorCode.MESSAGE_SUCCESS.getCode()))
        .data(response)
        .build();
  }
}
