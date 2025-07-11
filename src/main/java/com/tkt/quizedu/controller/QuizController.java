package com.tkt.quizedu.controller;

import java.util.List;
import java.util.UUID;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import com.tkt.quizedu.component.Translator;
import com.tkt.quizedu.data.constant.EndpointConstant;
import com.tkt.quizedu.data.constant.ErrorCode;
import com.tkt.quizedu.data.dto.request.QuestionMultipleChoiceRequest;
import com.tkt.quizedu.data.dto.request.QuizCreationRequest;
import com.tkt.quizedu.data.dto.request.UpdateQuestionMultipleChoiceRequest;
import com.tkt.quizedu.data.dto.response.QuizCreationResponse;
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
  SuccessApiResponse<QuizCreationResponse> save(@RequestBody @Valid QuizCreationRequest req) {
    return SuccessApiResponse.<QuizCreationResponse>builder()
        .code(ErrorCode.MESSAGE_SUCCESS.getCode())
        .status(HttpStatus.CREATED.value())
        .message(Translator.toLocale(ErrorCode.MESSAGE_SUCCESS.getCode()))
        .data(quizService.save(req))
        .build();
  }

  @GetMapping
  @ResponseStatus(HttpStatus.OK)
  SuccessApiResponse<List<QuizCreationResponse>> getAll() {
    return SuccessApiResponse.<List<QuizCreationResponse>>builder()
        .code(ErrorCode.MESSAGE_SUCCESS.getCode())
        .status(HttpStatus.OK.value())
        .message(Translator.toLocale(ErrorCode.MESSAGE_SUCCESS.getCode()))
        .data(quizService.getAll())
        .build();
  }

  @GetMapping("/{id}")
  @ResponseStatus(HttpStatus.OK)
  SuccessApiResponse<QuizCreationResponse> getById(@PathVariable String id) {
    return SuccessApiResponse.<QuizCreationResponse>builder()
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
  SuccessApiResponse<QuizCreationResponse> addMultipleChoiceQuizQuestion(
      @PathVariable String quizId,
      @RequestBody @Valid List<QuestionMultipleChoiceRequest> questions) {
    QuizCreationResponse response = quizService.addMultipleChoiceQuizQuestion(quizId, questions);
    return SuccessApiResponse.<QuizCreationResponse>builder()
        .code(ErrorCode.MESSAGE_SUCCESS.getCode())
        .status(HttpStatus.OK.value())
        .message(Translator.toLocale(ErrorCode.MESSAGE_SUCCESS.getCode()))
        .data(response)
        .build();
  }

  @PutMapping("/multiple-choice-quizzes/{quizId}/questions")
  @ResponseStatus(HttpStatus.OK)
  SuccessApiResponse<QuizCreationResponse> updateMultipleChoiceQuizQuestion(
      @PathVariable String quizId,
      @RequestBody @Valid List<UpdateQuestionMultipleChoiceRequest> questions) {
    // Assuming the service method is implemented to handle updates
    QuizCreationResponse response = quizService.updateMultipleChoiceQuizQuestion(quizId, questions);
    return SuccessApiResponse.<QuizCreationResponse>builder()
        .code(ErrorCode.MESSAGE_SUCCESS.getCode())
        .status(HttpStatus.OK.value())
        .message(Translator.toLocale(ErrorCode.MESSAGE_SUCCESS.getCode()))
        .data(response)
        .build();
  }
}
