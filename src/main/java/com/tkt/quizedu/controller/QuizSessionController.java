package com.tkt.quizedu.controller;

import com.tkt.quizedu.data.collection.MultipleChoiceQuiz;
import com.tkt.quizedu.data.dto.request.HistoryQuizSessionRequest;
import com.tkt.quizedu.data.dto.request.SubmitQuizRequest;
import com.tkt.quizedu.data.dto.response.HistoryQuizSessionResponse;
import com.tkt.quizedu.service.quiz.IQuizService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tkt.quizedu.component.Translator;
import com.tkt.quizedu.data.constant.EndpointConstant;
import com.tkt.quizedu.data.constant.ErrorCode;
import com.tkt.quizedu.data.dto.request.JoinQuizSessionRequest;
import com.tkt.quizedu.data.dto.request.QuizSessionRequest;
import com.tkt.quizedu.data.dto.response.QuizSessionResponse;
import com.tkt.quizedu.data.dto.response.SuccessApiResponse;
import com.tkt.quizedu.service.quizsession.IQuizSessionService;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@RestController
@RequestMapping(EndpointConstant.ENDPOINT_QUIZ_SESSION)
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Slf4j(topic = "QUIZ-SESSION-CONTROLLER")
@Tag(
    name = "QuizSession Management",
    description = "APIs for QuizSession registration and management")
public class QuizSessionController {
  IQuizSessionService quizSessionService;
  IQuizService quizService;

  @PostMapping
  public SuccessApiResponse<QuizSessionResponse> createQuizSession(
      @RequestBody QuizSessionRequest quizSessionRequest) {
    QuizSessionResponse response = quizSessionService.createQuizSession(quizSessionRequest);
    return SuccessApiResponse.<QuizSessionResponse>builder()
        .code(ErrorCode.MESSAGE_SUCCESS.getCode())
        .status(HttpStatus.CREATED.value())
        .message(Translator.toLocale(ErrorCode.MESSAGE_SUCCESS.getCode()))
        .data(response)
        .build();
  }

  @PostMapping("/joinQuizSession")
  public SuccessApiResponse<Boolean> joinQuizSession(
      @RequestBody JoinQuizSessionRequest joinQuizSessionRequest) {
    Boolean response =
        quizSessionService.joinQuizSession(joinQuizSessionRequest.accessCode());
    return SuccessApiResponse.<Boolean>builder()
        .code(ErrorCode.MESSAGE_SUCCESS.getCode())
        .status(HttpStatus.OK.value())
        .message(Translator.toLocale(ErrorCode.MESSAGE_SUCCESS.getCode()))
        .data(response)
        .build();
  }

  @PostMapping("/submitQuizSession")
    public SuccessApiResponse<Integer> submitQuizSession(
        @RequestBody SubmitQuizRequest submitQuizRequest) {
        int score = quizSessionService.submitQuizSession(submitQuizRequest);
        return SuccessApiResponse.<Integer>builder()
            .code(ErrorCode.MESSAGE_SUCCESS.getCode())
            .status(HttpStatus.OK.value())
            .message(Translator.toLocale(ErrorCode.MESSAGE_SUCCESS.getCode()))
            .data(score)
            .build();
    }

    @PostMapping("/history")
    public SuccessApiResponse<HistoryQuizSessionResponse> getHistoryByUserId(
        @RequestBody HistoryQuizSessionRequest historyQuizSessionRequest) {
        return SuccessApiResponse.<HistoryQuizSessionResponse>builder()
            .code(ErrorCode.MESSAGE_SUCCESS.getCode())
            .status(HttpStatus.OK.value())
            .message(Translator.toLocale(ErrorCode.MESSAGE_SUCCESS.getCode()))
            .data(
                quizSessionService.getQuizSessionHistory(
                    historyQuizSessionRequest.quizSessionId(),
                    historyQuizSessionRequest.userId()))
            .build();
    }
}
