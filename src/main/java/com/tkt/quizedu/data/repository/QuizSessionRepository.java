package com.tkt.quizedu.data.repository;

import org.springframework.stereotype.Repository;

import com.tkt.quizedu.data.base.BaseRepository;
import com.tkt.quizedu.data.collection.QuizSession;

@Repository
public interface QuizSessionRepository extends BaseRepository<QuizSession, String> {
  QuizSession findByAccessCode(String accessCode);
}
