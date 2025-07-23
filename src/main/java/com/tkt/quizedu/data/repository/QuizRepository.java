package com.tkt.quizedu.data.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.stereotype.Repository;

import com.tkt.quizedu.data.base.BaseRepository;
import com.tkt.quizedu.data.collection.Quiz;

@Repository
public interface QuizRepository extends BaseRepository<Quiz, String> {
  @Aggregation(
      pipeline = {
        "{ '$match': { 'teacher_id': ?0 } }",
        "{ '$skip': ?#{#pageable.offset} }",
        "{ '$limit': ?#{#pageable.pageSize} }"
      })
  List<Quiz> findAllByTeacherId(String teacherId, Pageable pageable);

  // Thêm method đếm tổng số:
  long countByTeacherId(String teacherId);
}
