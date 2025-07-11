package com.tkt.quizedu.data.collection;

import com.tkt.quizedu.data.base.StringIdentityCollection;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serial;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Document(collection = "multipleChoiceQuiz")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class MultipleChoiceQuiz extends StringIdentityCollection {
    @Serial
    private static final long serialVersionUID = -5465733518693373245L;
    @Id
    String id;
    String name;
    List<Question> questions;
    String quizId;
    @Data
    public static class Question {
        UUID questionId;
        @Indexed(unique = true)
        String questionText;
        String hint;
        int timeLimit;
        boolean allowMultipleAnswers;
        int points;
        List<AnswerOption> answers;
        List<AnswerParticipant> answerParticipants;
        @Data
        public static class AnswerParticipant {
            String userId;
            String answer;
            String correctAnswer;
            boolean correct;
        }
        @Data
        public static class AnswerOption {
            String answerText;
            boolean correct;
        }
    }

}
