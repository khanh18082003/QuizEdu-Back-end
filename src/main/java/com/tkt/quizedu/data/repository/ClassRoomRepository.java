package com.tkt.quizedu.data.repository;

import java.util.List;

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
        "{ '$lookup': { 'from': 'users', 'localField': 'teacherId', 'foreignField': '_id', 'as': 'teacher' } }",
        "{ '$unwind': { 'path': '$teacher', 'preserveNullAndEmptyArrays': true } }",
        "{ '$project': { "
            + "'id': '$_id', "
            + "'name': 1, "
            + "'description': 1, "
            + "'teacher': { "
            + "'id': '$teacher._id', "
            + "'firstName': '$teacher.firstName', "
            + "'lastName': '$teacher.lastName', "
            + "'email': '$teacher.email', "
            + "'avatar': '$teacher.avatar', "
            + "}, "
            + "'isActive': 1, "
            + "'createdAt': 1 "
            + "} }",
        "{ '$skip': ?#{#pageable.offset} }",
        "{ '$limit': ?#{#pageable.pageSize} }"
      })
  List<ClassroomBaseResponse> findClassroomResponsesByIds(List<String> ids, Pageable pageable);
  
  Optional<ClassRoom> findByClassCode(String classCode);

}
