package com.tkt.quizedu.service.classroom;

import java.util.List;

import org.springframework.stereotype.Service;

import com.tkt.quizedu.data.collection.ClassRoom;
import com.tkt.quizedu.data.dto.request.ClassRoomRequest;
import com.tkt.quizedu.data.dto.response.ClassRoomResponse;
import com.tkt.quizedu.data.mapper.ClassRoomMapper;
import com.tkt.quizedu.data.repository.ClassRoomRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Slf4j(topic = "CLASSROOM-SERVICE")
public class ClassRoomServiceImpl implements IClassRoomService {
  ClassRoomRepository classRoomRepository;
  ClassRoomMapper classRoomMapper;

  @Override
  public ClassRoomResponse createClassRoom(ClassRoomRequest classRoomRequest) {
    ClassRoom classRoom = classRoomMapper.toClassRoom(classRoomRequest);
    classRoom.setCreatedAt(java.time.LocalDate.now());
    return classRoomMapper.toClassRoomResponse(classRoomRepository.save(classRoom));
  }

  @Override
  public Boolean joinClassRoom(String classRoomId, String studentId) {
    ClassRoom classRoom =
        classRoomRepository
            .findById(classRoomId)
            .orElseThrow(() -> new RuntimeException("Classroom not found"));
    List<String> studentIds = classRoom.getStudentIds();
    if (studentIds.contains(studentId)) {
      return false; // Student already in the classroom
    }
    studentIds.add(studentId);
    classRoom.setStudentIds(studentIds);
    classRoomRepository.save(classRoom);
    return true; // Successfully added student to the classroom
  }

  @Override
  public Boolean assignQuizToClassroom(String classRoomId, String quizId) {
    ClassRoom classRoom =
        classRoomRepository
            .findById(classRoomId)
            .orElseThrow(() -> new RuntimeException("Classroom not found"));
    List<String> assignedQuizIds = classRoom.getAssignedQuizIds();
    if (assignedQuizIds.contains(quizId)) {
      return false;
    }
    assignedQuizIds.add(quizId);
    classRoom.setAssignedQuizIds(assignedQuizIds);
    classRoomRepository.save(classRoom);
    return true;
  }
}
