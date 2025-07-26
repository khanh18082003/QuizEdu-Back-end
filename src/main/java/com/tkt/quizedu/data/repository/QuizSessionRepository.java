package com.tkt.quizedu.data.repository;

import com.tkt.quizedu.data.base.BaseRepository;
import com.tkt.quizedu.data.collection.QuizSession;
import com.tkt.quizedu.data.constant.SessionStatus;

public interface QuizSessionRepository extends BaseRepository<QuizSession, String> {
  QuizSession findByAccessCodeAndStatus(String accessCode, SessionStatus status);
  boolean existsByAccessCodeAndStatus(String accessCode, SessionStatus status);
}
