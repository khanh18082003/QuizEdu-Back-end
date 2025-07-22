package com.tkt.quizedu.service.classroom;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tkt.quizedu.data.collection.ClassRoom;
import com.tkt.quizedu.data.collection.Quiz;
import com.tkt.quizedu.data.dto.request.ClassRoomRequest;
import com.tkt.quizedu.data.dto.response.ClassRoomResponse;
import com.tkt.quizedu.data.dto.response.ClassroomBaseResponse;
import com.tkt.quizedu.data.mapper.ClassRoomMapper;
import com.tkt.quizedu.data.repository.ClassRoomRepository;
import com.tkt.quizedu.data.repository.QuizRepository;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Builder
@Slf4j(topic = "CLASSROOM-SERVICE")
public class ClassRoomServiceImpl implements IClassRoomService {
  ClassRoomRepository classRoomRepository;
  ClassRoomMapper classRoomMapper;
  QuizRepository quizRepository;

  @Override
  public ClassRoomResponse createClassRoom(ClassRoomRequest classRoomRequest) {
    ClassRoom classRoom = classRoomMapper.toClassRoom(classRoomRequest);
    classRoom.setCreatedAt(java.time.LocalDate.now());
    return classRoomMapper.toClassRoomResponse(classRoomRepository.save(classRoom));
  }

  @Override
  public ClassRoomResponse updateClassRoom(String classRoomId, ClassRoomRequest classRoomRequest) {
    ClassRoom classRoom =
        classRoomRepository
            .findById(classRoomId)
            .orElseThrow(() -> new RuntimeException("Classroom not found"));
    classRoom.setName(classRoomRequest.name());
    classRoom.setDescription(classRoomRequest.description());
    classRoom.setActive(classRoomRequest.isActive());
    classRoom.setAssignedQuizIds(classRoomRequest.assignedQuizIds());

    return classRoomMapper.toClassRoomResponse(classRoomRepository.save(classRoom));
  }

  @Override
  @Transactional
  public void deleteClassRoom(String classRoomId) {
    ClassRoom classRoom =
        classRoomRepository
            .findById(classRoomId)
            .orElseThrow(() -> new RuntimeException("Classroom not found"));
    classRoomRepository.delete(classRoom);
  }

  @Override
  public Page<ClassroomBaseResponse> getClassroomByIds(List<String> ids, Pageable pageable) {
    long total = classRoomRepository.countClassroomsByIds(ids).size();

    // Lấy data với pagination
    List<ClassroomBaseResponse> content =
        classRoomRepository.findClassroomResponsesByIds(ids, pageable);

    return new PageImpl<>(content, pageable, total);
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
  @Transactional
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
    // Import Quiz and add the classRoomId to the quiz's classIds
    Quiz quiz =
        quizRepository.findById(quizId).orElseThrow(() -> new RuntimeException("Quiz not found"));
    List<String> classRoomIds = quiz.getClassIds();
    if (!classRoomIds.contains(classRoomId)) {
      classRoomIds.add(classRoomId);
      quiz.setClassIds(classRoomIds);
      quizRepository.save(quiz);
    }

    return true;
  }
}
