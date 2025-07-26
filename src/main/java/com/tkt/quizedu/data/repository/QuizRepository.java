package com.tkt.quizedu.data.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.tkt.quizedu.data.base.BaseRepository;
import com.tkt.quizedu.data.collection.Quiz;

@Repository
public interface QuizRepository extends BaseRepository<Quiz, String> {

  Page<Quiz> findAllByTeacherId(String teacherId, Pageable pageable);
}
