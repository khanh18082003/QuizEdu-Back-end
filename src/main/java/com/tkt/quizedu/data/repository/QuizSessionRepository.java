package com.tkt.quizedu.data.repository;

import org.springframework.stereotype.Repository;

import com.tkt.quizedu.data.base.BaseRepository;
import com.tkt.quizedu.data.collection.QuizSession;
import com.tkt.quizedu.data.constant.SessionStatus;

@Repository
public interface QuizSessionRepository extends BaseRepository<QuizSession, String> {
  QuizSession findByAccessCodeAndStatus(String accessCode, SessionStatus status);
  boolean existsByAccessCodeAndStatus(String accessCode, SessionStatus status);
}
