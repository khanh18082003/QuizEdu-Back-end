package com.tkt.quizedu.data.collection;

import java.io.Serial;
import java.time.LocalDate;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.tkt.quizedu.data.base.StringIdentityCollection;
import com.tkt.quizedu.data.constant.SessionStatus;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Document(collection = "quizSessions")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class QuizSession extends StringIdentityCollection {
  @Serial private static final long serialVersionUID = -5465733518693373245L;
  @Id String id;
  String quizId;
  String classId;
  String teacherId;
  SessionStatus status;
  String accessCode;
  LocalDate startTime;
  LocalDate endTime;
  List<Participant> participants;

  @Data
  public static class Participant {
    String userId;
    LocalDate joinedAt;
  }
}
