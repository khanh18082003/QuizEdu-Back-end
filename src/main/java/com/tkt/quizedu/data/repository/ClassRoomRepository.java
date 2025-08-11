package com.tkt.quizedu.data.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Update;
import org.springframework.stereotype.Repository;

import com.tkt.quizedu.data.base.BaseRepository;
import com.tkt.quizedu.data.collection.ClassRoom;
import com.tkt.quizedu.data.collection.Quiz;
import com.tkt.quizedu.data.dto.response.ClassroomBaseResponse;
import com.tkt.quizedu.data.dto.response.QuizBaseResponse;
import com.tkt.quizedu.data.dto.response.UserBaseResponse;

@Repository
public interface ClassRoomRepository extends BaseRepository<ClassRoom, String> {

  boolean existsByClassCode(String classCode);

  @Aggregation(pipeline = {"{ '$match': { '_id': { '$in': ?0 } } }"})
  List<ClassRoom> countClassroomsByIds(List<String> ids);

  @Aggregation(
      pipeline = {
        "{ '$match': { '_id': { '$in': ?0 } } }",
        "{ '$addFields': { 'teacherObjectId': { '$toObjectId': '$teacher_id' } } }",
        "{ '$lookup': { 'from': 'users', 'localField': 'teacherObjectId', 'foreignField': '_id', 'as': 'teacher' } }",
        "{ '$unwind': { 'path': '$teacher', 'preserveNullAndEmptyArrays': true } }",
        "{ '$project': { "
            + "'id': '$_id', "
            + "'name': 1, "
            + "'description': 1, "
            + "'class_code': 1, "
            + "'teacher': { "
            + "'id': '$teacher._id', "
            + "'email': '$teacher.email', "
            + "'first_name': '$teacher.first_name', "
            + "'last_name': '$teacher.last_name', "
            + "'display_name': '$teacher.display_name', "
            + "'avatar': '$teacher.avatar', "
            + "'is_active': '$teacher.is_active', "
            + "'subjects': '$teacher.subjects', "
            + "'experience': '$teacher.experience', "
            + "'school_name': '$teacher.school_name' "
            + "}, "
            + "'is_active': 1, "
            + "'created_at': 1 "
            + "} }",
        "{ '$sort': { 'created_at': -1 } }",
        "{ '$skip': ?1 }",
        "{ '$limit': ?2 }"
      })
  List<ClassroomBaseResponse> findClassroomResponsesByIds(
      List<String> ids, long offset, int pageSize);

  @Aggregation(
      pipeline = {
        "{ '$match': { '_id': ?0 } }",
        "{ '$addFields': { 'teacherObjectId': { '$toObjectId': '$teacher_id' } } }",
        "{ '$lookup': { 'from': 'users', 'localField': 'teacherObjectId', 'foreignField': '_id', 'as': 'teacher' } }",
        "{ '$unwind': { 'path': '$teacher', 'preserveNullAndEmptyArrays': true } }",
        "{ '$project': { "
            + "'id': '$_id', "
            + "'name': 1, "
            + "'description': 1, "
            + "'class_code': 1, "
            + "'teacher': { "
            + "'id': '$teacher._id', "
            + "'email': '$teacher.email', "
            + "'first_name': '$teacher.first_name', "
            + "'last_name': '$teacher.last_name', "
            + "'display_name': '$teacher.display_name', "
            + "'avatar': '$teacher.avatar', "
            + "'is_active': '$teacher.is_active', "
            + "'subjects': '$teacher.subjects', "
            + "'experience': '$teacher.experience', "
            + "'school_name': '$teacher.school_name' "
            + "}, "
            + "'is_active': 1, "
            + "'created_at': 1 "
            + "} }"
      })
  Optional<ClassroomBaseResponse> findClassroomResponseById(String classRoomId);

  Optional<ClassRoom> findByClassCode(String classCode);

  List<ClassRoom> findByTeacherId(String teacherId);

  @Query(value = "{'_id': ?0}")
  @Update(value = "{'$pull': {'student_ids': ?1}}")
  void removeStudentFromClassroom(String classRoomId, String studentId);

  @Aggregation(
      pipeline = {
        "{ '$match': { '_id': ?0 } }",
        "{ '$addFields': { 'studentObjectIds': { '$map': { 'input': '$student_ids', 'as': 'id', 'in': { '$toObjectId': '$$id' } } } } }",
        "{ '$lookup': { 'from': 'users', 'localField': 'studentObjectIds', 'foreignField': '_id', 'as': 'students' } }",
        "{ '$unwind': { 'path': '$students', 'preserveNullAndEmptyArrays': false } }",
        "{ '$sort': { 'students.first_name': 1 } }",
        "{ '$project': { "
            + "'id': '$students._id', "
            + "'email': '$students.email', "
            + "'first_name': '$students.first_name', "
            + "'last_name': '$students.last_name', "
            + "'display_name': '$students.display_name', "
            + "'avatar': '$students.avatar', "
            + "'is_active': '$students.is_active', "
            + "'role': '$students.role', "
            + "'created_at': '$students.created_at', "
            + "'updated_at': '$students.updated_at' "
            + "} }",
        "{ '$skip': ?1 }",
        "{ '$limit': ?2 }"
      })
  List<UserBaseResponse> findAllStudentsInClassRoom(String classRoomId, long skip, int limit);

  @Aggregation(
      pipeline = {
        "{ '$match': { '_id': ?0 } }",
        "{ '$addFields': { 'quizObjectIds': { '$map': { 'input': '$assigned_quiz_ids', 'as': 'id', 'in': { '$toObjectId': '$$id' } } } } }",
        "{ '$lookup': { 'from': 'quiz', 'localField': 'quizObjectIds', 'foreignField': '_id', 'as': 'quizzes' } }",
        "{ '$unwind': { 'path': '$quizzes', 'preserveNullAndEmptyArrays': false } }",
        "{ '$match': { 'quizzes.is_public': true } }",
        "{ '$addFields': { 'quizIdStr': { '$toString': '$quizzes._id' } } }", // Convert ObjectId to
        // string
        "{ '$lookup': { 'from': 'multipleChoiceQuiz', 'localField': 'quizIdStr', 'foreignField': 'quiz_id', 'as': 'multipleChoiceQuiz' } }",
        "{ '$lookup': { 'from': 'matchingQuiz', 'localField': 'quizIdStr', 'foreignField': 'quiz_id', 'as': 'matchingQuiz' } }",
        "{ '$project': { "
            + "'id': '$quizzes._id', "
            + "'name': '$quizzes.name', "
            + "'description': '$quizzes.description', "
            + "'is_active': '$quizzes.is_active', "
            + "'is_public': '$quizzes.is_public', "
            + "'number_of_multiple_choice_questions': { '$cond': [ { '$eq': [ { '$size': '$multipleChoiceQuiz' }, 0 ] }, 0, { '$size': { '$arrayElemAt': [ '$multipleChoiceQuiz.questions', 0 ] } } ] }, "
            + "'number_of_matching_questions': { '$cond': [ { '$eq': [ { '$size': '$matchingQuiz' }, 0 ] }, 0, { '$size': { '$arrayElemAt': [ '$matchingQuiz.match_pairs', 0 ] } } ] } "
            + "} }",
        "{ '$sort': { 'name': 1 } }",
        "{ '$skip': ?1 }",
        "{ '$limit': ?2 }"
      })
  List<QuizBaseResponse> findAllByClassroomId(String classroomId, long skip, int limit);

  @Aggregation(
      pipeline = {
        "{ '$match': { '_id': ?0 } }",
        "{ '$addFields': { 'quizObjectIds': { '$map': { 'input': '$assigned_quiz_ids', 'as': 'id', 'in': { '$toObjectId': '$$id' } } } } }",
        "{ '$lookup': { 'from': 'quiz', 'localField': 'quizObjectIds', 'foreignField': '_id', 'as': 'quizzes' } }",
        "{ '$unwind': { 'path': '$quizzes', 'preserveNullAndEmptyArrays': false } }",
        "{ '$match': { 'quizzes.is_public': true } }"
      })
  List<Quiz> countPublicQuizzesByClassroomId(String classroomId);
}
