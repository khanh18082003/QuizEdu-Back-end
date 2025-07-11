package com.tkt.quizedu.data.repository;

import com.tkt.quizedu.data.base.BaseRepository;
import com.tkt.quizedu.data.collection.MultipleChoiceQuiz;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface MultipleChoiceQuizRepository extends BaseRepository<MultipleChoiceQuiz, String> {
    // Define any additional methods specific to MultipleChoiceQuizRepository if needed
    MultipleChoiceQuiz findByQuizId(String quizId);
    Optional<MultipleChoiceQuiz> findByQuestionsQuestionId(UUID questionId);

}
