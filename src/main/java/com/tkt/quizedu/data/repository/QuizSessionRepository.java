package com.tkt.quizedu.data.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.stereotype.Repository;

import com.tkt.quizedu.data.base.BaseRepository;
import com.tkt.quizedu.data.collection.QuizSession;
import com.tkt.quizedu.data.dto.response.QuizDetailResponse;

@Repository
public interface QuizSessionRepository extends BaseRepository<QuizSession, String> {
  QuizSession findByAccessCode(String accessCode);

  @Aggregation(
      pipeline = {
        "{ '$match': { 'class_id': ?0 } }",
        "{ '$addFields': { 'quizObjectId': { '$toObjectId': '$quiz_id' } } }",
        "{ '$lookup': { 'from': 'quiz', 'localField': 'quizObjectId', 'foreignField': '_id', 'as': 'quiz' } }",
        "{ '$unwind': { 'path': '$quiz', 'preserveNullAndEmptyArrays': false } }",
        "{ '$project': { "
            + "'id': '$quiz._id', "
            + "'name': '$quiz.name', "
            + "'description': '$quiz.description', "
            + "'is_active': '$quiz.is_active', "
            + "'quiz_session_id': '$_id', "
            + "'start_time': '$start_time', "
            + "'end_time': '$end_time' "
            + "} }",
        "{ '$skip': ?#{#pageable.offset} }",
        "{ '$limit': ?#{#pageable.pageSize} }"
      })
  List<QuizDetailResponse> findAllQuizzSessionByClassId(String classId, Pageable pageable);
}
