package com.tkt.quizedu.service.classroom;

import com.tkt.quizedu.data.dto.request.ClassRoomRequest;
import com.tkt.quizedu.data.dto.response.ClassRoomResponse;

public interface IClassRoomService {
  ClassRoomResponse createClassRoom(ClassRoomRequest classRoomRequest);

  Boolean joinClassRoom(String classRoomId);

  Boolean assignQuizToClassroom(String classRoomId, String quizId);

  ClassRoomResponse updateClassRoom(String classRoomId, ClassRoomRequest classRoomRequest);

  void deleteClassRoom(String classRoomId);
}
