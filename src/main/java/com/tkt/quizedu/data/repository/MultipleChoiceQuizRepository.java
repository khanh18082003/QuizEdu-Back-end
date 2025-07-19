package com.tkt.quizedu.data.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.tkt.quizedu.data.base.BaseRepository;
import com.tkt.quizedu.data.collection.MultipleChoiceQuiz;

@Repository
public interface MultipleChoiceQuizRepository extends BaseRepository<MultipleChoiceQuiz, String> {
  // Define any additional methods specific to MultipleChoiceQuizRepository if needed
  MultipleChoiceQuiz findByQuizId(String quizId);

  Boolean existsByQuizId(String quizId);

  Optional<MultipleChoiceQuiz> findByQuestionsQuestionId(UUID questionId);
}
