package com.tkt.quizedu.component;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import com.tkt.quizedu.data.dto.response.UserBaseResponse;
import com.tkt.quizedu.data.dto.response.UserSubmitResponse;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class WebSocketPublisher {
  private final SimpMessagingTemplate simpMessagingTemplate;

  public void publishStartExam(String sessionId) {
    String destination = "/topic/start-exam/" + sessionId;
    simpMessagingTemplate.convertAndSend(destination, true);
  }

  public void publishJoinQuizSession(String sessionId, UserBaseResponse student) {
    String destination = "/topic/join-quiz-session/" + sessionId;
    simpMessagingTemplate.convertAndSend(destination, student);
  }

  public void publishCloseQuizSession(String sessionId) {
    String destination = "/topic/close-quiz-session/" + sessionId;
    simpMessagingTemplate.convertAndSend(destination, true);
  }

  public void publishSubmitQuizSession(String sessionId, UserSubmitResponse student) {
    String destination = "/topic/submit-quiz-session/" + sessionId;
    simpMessagingTemplate.convertAndSend(destination, student);
  }
}
