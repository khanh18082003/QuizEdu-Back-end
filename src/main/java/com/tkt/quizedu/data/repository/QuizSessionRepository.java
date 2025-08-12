package com.tkt.quizedu.data.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.stereotype.Repository;

import com.tkt.quizedu.data.base.BaseRepository;
import com.tkt.quizedu.data.collection.QuizSession;
import com.tkt.quizedu.data.constant.SessionStatus;
import com.tkt.quizedu.data.dto.response.QuizDetailResponse;
import com.tkt.quizedu.data.dto.response.ScoreQuizSessionStudentResponse;

@Repository
public interface QuizSessionRepository extends BaseRepository<QuizSession, String> {
  Optional<QuizSession> findByAccessCode(String accessCode);

  boolean existsByAccessCodeAndStatus(String accessCode, SessionStatus status);

  @Aggregation(
      pipeline = {
        "{ '$match': { 'class_id': ?0, $expr: { $or: [ { $eq: [?3, null] }, { $eq: ['$status', ?3] } ] } } }",
        "{ '$addFields': { 'quizObjectId': { '$toObjectId': '$quiz_id' } } }",
        "{ '$lookup': { 'from': 'quiz', 'localField': 'quizObjectId', 'foreignField': '_id', 'as': 'quiz' } }",
        "{ '$unwind': { 'path': '$quiz', 'preserveNullAndEmptyArrays': false } }",
        "{'$sort': { 'created_at': -1 } }",
        "{ '$project': { "
            + "'id': '$quiz._id', "
            + "'name': '$quiz.name', "
            + "'description': '$quiz.description', "
            + "'quiz_session_id': '$_id', "
            + "'access_code': '$access_code', "
            + "'status': '$status', "
            + "'start_time': '$start_time', "
            + "'end_time': '$end_time' ,"
            + "'created_at': '$created_at'"
            + "} }",
        "{ '$skip': ?1 }",
        "{ '$limit': ?2 }"
      })
  List<QuizDetailResponse> findAllQuizSessionByClassId(
      String classId, long offset, int limit, SessionStatus status);

  @Aggregation(
      pipeline = {
        "{ '$match': { 'class_id': ?0, $expr: { $or: [ { $eq: [?1, null] }, { $eq: ['$status', ?1] } ] } } }"
      })
  List<QuizSession> totalQuizSessionByClassId(String classId, SessionStatus status);

  @Aggregation(
      pipeline = {
        "{ '$match': { 'participants': { '$elemMatch': { 'user_id': ?0 } } } }",
        "{ '$addFields': { 'quizObjectId': { '$toObjectId': '$quiz_id' } } }",
        "{ '$addFields': { 'classObjectId': { '$toObjectId': '$class_id' } } }",
        "{ '$lookup': { 'from': 'quiz', 'localField': 'quizObjectId', 'foreignField': '_id', 'as': 'quiz' } }",
        "{ '$lookup': { 'from': 'classRooms', 'localField': 'classObjectId', 'foreignField': '_id', 'as': 'classroom' } }",
        "{ '$unwind': { 'path': '$quiz', 'preserveNullAndEmptyArrays': false } }",
        "{ '$unwind': { 'path': '$classroom', 'preserveNullAndEmptyArrays': false } }",
        "{ '$addFields': { 'participantScore': { '$filter': { 'input': '$participants', 'as': 'p', 'cond': { '$eq': ['$$p.user_id', ?0] } } } } }",
        "{ '$project': { "
            + "'score': { '$arrayElemAt': ['$participantScore.score', 0] }, "
            + "'quiz_session_id': '$_id', "
            + "'quiz_id': '$quizId', "
            + "'quiz_name': '$quiz.name', "
            + "'classroom_id': '$classId', "
            + "'classroom_name': '$classroom.name', "
            + "'start_time': '$start_time', "
            + "'end_time': '$end_time' "
            + "} }",
        "{ '$sort': { 'startTime': -1 } }"
      })
  List<ScoreQuizSessionStudentResponse> findByStudentId(String studentId);
  @Aggregation(pipeline = {"{ '$match': { 'class_id': ?0 } }"})
  List<QuizSession> totalQuizSessionByClassId(String classId);

  List<QuizSession> findByQuizIdAndClassId(String quizId, String classId);
}
