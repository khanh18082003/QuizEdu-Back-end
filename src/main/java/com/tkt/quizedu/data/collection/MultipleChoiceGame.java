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

@Document(collection = "multipleChoiceGames")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class MultipleChoiceGame extends StringIdentityCollection {
    @Serial
    private static final long serialVersionUID = -5465733518693373245L;
    @Id
    String id;
    String name;
    boolean allowMultipleAnswers;
    List<Question> questions;
    String GameId;
    @Data
    public static class Question {
        @Id
        String id;
        @Indexed(unique = true)
        String questionText;
        String hint;
        int timeLimit;
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
