package com.tkt.quizedu.data.repository;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.tkt.quizedu.data.base.BaseRepository;
import com.tkt.quizedu.data.collection.MatchingQuiz;

@Repository
public interface MatchingQuizRepository extends BaseRepository<MatchingQuiz, String> {
  // Define any additional methods specific to MatchingQuizRepository if needed
  MatchingQuiz findByQuizId(String quizId);

  Optional<MatchingQuiz> findByQuizSessionId(String quizSessionId);

  Boolean existsByQuizId(String quizId);
}
