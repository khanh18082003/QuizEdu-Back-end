package com.tkt.quizedu.data.repository;

import com.tkt.quizedu.data.base.BaseRepository;
import com.tkt.quizedu.data.collection.MatchingQuiz;
import org.springframework.stereotype.Repository;

@Repository
public interface MatchingQuizRepository extends BaseRepository<MatchingQuiz, String> {
    // Define any additional methods specific to MatchingQuizRepository if needed
    MatchingQuiz findByQuizId(String quizId);
    Boolean existsByQuizId(String quizId);
}
