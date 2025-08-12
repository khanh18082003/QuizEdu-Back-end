package com.tkt.quizedu.service.classroom;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.tkt.quizedu.data.dto.request.ClassRoomRequest;
import com.tkt.quizedu.data.dto.request.InviteStudentsToClassRoomRequest;
import com.tkt.quizedu.data.dto.response.*;

public interface IClassRoomService {
  ClassRoomResponse createClassRoom(ClassRoomRequest classRoomRequest);

  Boolean joinClassRoom(String classRoomId);

  Boolean assignQuizToClassroom(String classRoomId, String quizId);

  ClassRoomResponse updateClassRoom(String classRoomId, ClassRoomRequest classRoomRequest);

  void deleteClassRoom(String classRoomId);

  Page<ClassroomBaseResponse> getClassroomByIds(List<String> ids, Pageable pageable);

  ClassroomDetailResponse getClassroomDetailById(String classRoomId);

  ClassroomBaseResponse getClassroomById(String classRoomId);

  PaginationResponse<UserBaseResponse> getAllStudentsInClassRoom(
      String classRoomId, int page, int pageSize);

  PaginationResponse<QuizDetailResponse> getQuizSessionsByClassRoomId(
      String classRoomId, int page, int pageSize, String... filters);

  void inviteStudentsToClassRoom(InviteStudentsToClassRoomRequest inviteStudentsToClassRoomRequest);

  void removeStudentFromClassRoom(String classRoomId, String studentId);

  void deleteQuizFromClassroom(String classRoomId, String quizId);
}
