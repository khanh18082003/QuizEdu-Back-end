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
    String QuizId;
    @Data
    public static class Question {
        @Indexed(unique = true)
        String questionText;
        String hint;
        int timeLimit;
        boolean allowMultipleAnswers;
        int points;
        Map<String, Boolean> answers;
        List<AnswerParticipant> answerParticipants;
        @Data
        public static class AnswerParticipant {
            String userId;
            String answer;
            String correctAnswer;
            boolean isCorrect;
        }
    }

}
