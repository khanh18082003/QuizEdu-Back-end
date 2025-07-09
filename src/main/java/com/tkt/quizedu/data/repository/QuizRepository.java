package com.tkt.quizedu.data.repository;

import com.tkt.quizedu.data.base.BaseRepository;
import com.tkt.quizedu.data.collection.Quiz;
import org.springframework.stereotype.Repository;

@Repository
public interface QuizRepository extends BaseRepository<Quiz, String> {
    // Define any additional methods specific to GameRepository if needed
}
