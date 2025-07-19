package com.tkt.quizedu.data.repository;

import com.tkt.quizedu.data.base.BaseRepository;
import com.tkt.quizedu.data.collection.QuizSession;

public interface QuizSessionRepository extends BaseRepository<QuizSession, String> {
    QuizSession findByAccessCode(String accessCode);
}
