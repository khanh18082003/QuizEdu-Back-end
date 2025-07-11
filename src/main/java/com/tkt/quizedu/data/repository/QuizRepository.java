package com.tkt.quizedu.data.repository;

import org.springframework.stereotype.Repository;

import com.tkt.quizedu.data.base.BaseRepository;
import com.tkt.quizedu.data.collection.Quiz;

@Repository
public interface QuizRepository extends BaseRepository<Quiz, String> {
  // Define any additional methods specific to GameRepository if needed
}
