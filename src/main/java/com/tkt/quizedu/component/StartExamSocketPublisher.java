package com.tkt.quizedu.component;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class StartExamSocketPublisher {
  private final SimpMessagingTemplate simpMessagingTemplate;

  public void publishStartExam(String sessionId) {
    String destination = "/topic/start-exam/" + sessionId;
    simpMessagingTemplate.convertAndSend(destination, true);
  }
}
