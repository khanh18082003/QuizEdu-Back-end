package com.tkt.quizedu.data.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.stereotype.Repository;

import com.tkt.quizedu.data.base.BaseRepository;
import com.tkt.quizedu.data.collection.ClassRoom;
import com.tkt.quizedu.data.dto.response.ClassroomBaseResponse;

@Repository
public interface ClassRoomRepository extends BaseRepository<ClassRoom, String> {
  // Method trả về List để đếm total
  @Aggregation(pipeline = {"{ '$match': { '_id': { '$in': ?0 } } }"})
  List<ClassRoom> countClassroomsByIds(List<String> ids);

  @Aggregation(
      pipeline = {
        "{ '$match': { '_id': { '$in': ?0 } } }",
        "{ '$addFields': { 'teacherObjectId': { '$toObjectId': '$teacherId' } } }",
        "{ '$lookup': { 'from': 'users', 'localField': 'teacherObjectId', 'foreignField': '_id', 'as': 'teacher' } }",
        "{ '$unwind': { 'path': '$teacher', 'preserveNullAndEmptyArrays': true } }",
        "{ '$project': { "
            + "'id': '$_id', "
            + "'name': 1, "
            + "'description': 1, "
            + "'teacher': { "
            + "'id': '$teacher._id', "
            + "'email': '$teacher.email', "
            + "'firstName': '$teacher.first_name', "
            + "'lastName': '$teacher.last_name', "
            + "'displayName': '$teacher.display_name', "
            + "'avatar': '$teacher.avatar', "
            + "'isActive': '$teacher.is_active', "
            + "'role': '$teacher.role', "
            + "'createdAt': '$teacher.created_at', "
            + "'updatedAt': '$teacher.updated_at', "
            + "'subjects': '$teacher.subjects', "
            + "'experience': '$teacher.experience', "
            + "'schoolName': '$teacher.school_name' "
            + "}, "
            + "'isActive': 1, "
            + "'createdAt': '$created_at' "
            + "} }",
        "{ '$skip': ?#{#pageable.offset} }",
        "{ '$limit': ?#{#pageable.pageSize} }"
      })
  List<ClassroomBaseResponse> findClassroomResponsesByIds(List<String> ids, Pageable pageable);

  Optional<ClassRoom> findByClassCode(String classCode);

  List<ClassRoom> findByTeacherId(String teacherId);
}
