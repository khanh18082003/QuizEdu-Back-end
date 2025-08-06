package com.tkt.quizedu.data.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.stereotype.Repository;

import com.tkt.quizedu.data.base.BaseRepository;
import com.tkt.quizedu.data.collection.QuizSession;
import com.tkt.quizedu.data.constant.SessionStatus;
import com.tkt.quizedu.data.dto.response.QuizDetailResponse;

@Repository
public interface QuizSessionRepository extends BaseRepository<QuizSession, String> {
  Optional<QuizSession> findByAccessCodeAndStatus(String accessCode, SessionStatus status);

  boolean existsByAccessCodeAndStatus(String accessCode, SessionStatus status);

  @Aggregation(
      pipeline = {
        "{ '$match': { 'class_id': ?0 } }",
        "{ '$addFields': { 'quizObjectId': { '$toObjectId': '$quiz_id' } } }",
        "{ '$lookup': { 'from': 'quiz', 'localField': 'quizObjectId', 'foreignField': '_id', 'as': 'quiz' } }",
        "{ '$unwind': { 'path': '$quiz', 'preserveNullAndEmptyArrays': false } }",
        "{'$sort': { 'created_at': -1 } }",
        "{ '$project': { "
            + "'id': '$quiz._id', "
            + "'name': '$quiz.name', "
            + "'description': '$quiz.description', "
            + "'quiz_session_id': '$_id', "
            + "'status': '$status', "
            + "'start_time': '$start_time', "
            + "'end_time': '$end_time' ,"
            + "'created_at': '$created_at'"
            + "} }",
        "{ '$skip': ?1 }",
        "{ '$limit': ?2 }"
      })
  List<QuizDetailResponse> findAllQuizSessionByClassId(String classId, long offset, int limit);

  @Aggregation(pipeline = {"{ '$match': { 'class_id': ?0 } }"})
  List<QuizSession> totalQuizSessionByClassId(String classId);
}
