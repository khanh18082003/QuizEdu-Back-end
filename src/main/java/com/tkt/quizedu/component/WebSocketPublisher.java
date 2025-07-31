package com.tkt.quizedu.component;

import com.tkt.quizedu.data.dto.response.UserBaseResponse;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

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
}
