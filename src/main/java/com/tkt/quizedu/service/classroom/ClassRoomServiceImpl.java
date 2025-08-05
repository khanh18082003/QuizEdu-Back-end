package com.tkt.quizedu.service.classroom;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tkt.quizedu.data.collection.ClassRoom;
import com.tkt.quizedu.data.collection.CustomUserDetail;
import com.tkt.quizedu.data.collection.Quiz;
import com.tkt.quizedu.data.collection.User;
import com.tkt.quizedu.data.constant.ErrorCode;
import com.tkt.quizedu.data.dto.request.ClassRoomRequest;
import com.tkt.quizedu.data.dto.request.InviteStudentsToClassRoomRequest;
import com.tkt.quizedu.data.dto.response.*;
import com.tkt.quizedu.data.mapper.ClassRoomMapper;
import com.tkt.quizedu.data.mapper.UserMapper;
import com.tkt.quizedu.data.repository.ClassRoomRepository;
import com.tkt.quizedu.data.repository.QuizRepository;
import com.tkt.quizedu.data.repository.QuizSessionRepository;
import com.tkt.quizedu.data.repository.UserRepository;
import com.tkt.quizedu.exception.QuizException;
import com.tkt.quizedu.utils.GenerateVerificationCode;
import com.tkt.quizedu.utils.SecurityUtils;

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
  UserRepository userRepository;
  ClassRoomMapper classRoomMapper;
  QuizRepository quizRepository;
  UserMapper userMapper;
  QuizSessionRepository quizSessionRepository;
  KafkaTemplate<String, String> kafkaTemplate;

  @Override
  public ClassRoomResponse createClassRoom(ClassRoomRequest classRoomRequest) {
    CustomUserDetail userDetail = SecurityUtils.getUserDetail();
    ClassRoom classRoom = classRoomMapper.toClassRoom(classRoomRequest);
    classRoom.setTeacherId(userDetail.getUser().getId());
    String classCode = GenerateVerificationCode.generateCode();
    while (classRoomRepository.existsByClassCode(classCode)) {
      classCode = GenerateVerificationCode.generateCode();
    }
    classRoom.setClassCode(classCode);
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
  public ClassroomDetailResponse getClassroomDetailById(String classRoomId) {
    ClassRoom classRoom =
        classRoomRepository
            .findById(classRoomId)
            .orElseThrow(() -> new QuizException(ErrorCode.MESSAGE_INVALID_ID));
    List<String> studentIds = classRoom.getStudentIds();
    List<User> students = userRepository.findAllById(studentIds);
    List<UserBaseResponse> studentProfiles =
        students.stream().map(userMapper::toUserBaseResponse).toList();
    List<String> assignedQuizIds = classRoom.getAssignedQuizIds();
    List<Quiz> quizzes = quizRepository.findAllById(assignedQuizIds);
    List<QuizBaseResponse> quizResponses =
        quizzes.stream()
            .map(
                quiz ->
                    QuizBaseResponse.builder()
                        .id(quiz.getId())
                        .name(quiz.getName())
                        .description(quiz.getDescription())
                        .isActive(quiz.isActive())
                        .build())
            .toList();
    UserBaseResponse teacher =
        userMapper.toUserBaseResponse(
            userRepository
                .findById(classRoom.getTeacherId())
                .orElseThrow(() -> new QuizException(ErrorCode.MESSAGE_INVALID_ID)));
    return ClassroomDetailResponse.builder()
        .id(classRoom.getId())
        .name(classRoom.getName())
        .description(classRoom.getDescription())
        .classCode(classRoom.getClassCode())
        .createdAt(classRoom.getCreatedAt())
        .teacher(teacher)
        .quiz(quizResponses)
        .students(studentProfiles)
        .build();
  }

  @Override
  public ClassroomBaseResponse getClassroomById(String classRoomId) {

    return classRoomRepository
        .findClassroomResponseById(classRoomId)
        .orElseThrow(() -> new QuizException(ErrorCode.MESSAGE_CLASSROOM_NOT_FOUND));
  }

  @Override
  public PaginationResponse<UserBaseResponse> getAllStudentsInClassRoom(
      String classRoomId, int page, int pageSize) {
    ClassRoom classRoom =
        classRoomRepository
            .findById(classRoomId)
            .orElseThrow(() -> new QuizException(ErrorCode.MESSAGE_INVALID_ID));
    List<String> studentIds = classRoom.getStudentIds();
    Pageable pageable = PageRequest.of(page - 1, pageSize);

    List<UserBaseResponse> students =
        classRoomRepository.findAllStudentsInClassRoom(classRoomId, pageable);
    Page<UserBaseResponse> studentPage = new PageImpl<>(students, pageable, studentIds.size());

    return PaginationResponse.<UserBaseResponse>builder()
        .page(studentPage.getNumber())
        .pageSize(studentPage.getSize())
        .pages(studentPage.getTotalPages())
        .total(studentPage.getTotalElements())
        .data(studentPage.getContent())
        .build();
  }

  @Override
  public PaginationResponse<QuizDetailResponse> getQuizSessionsByClassRoomId(
      String classRoomId, int page, int pageSize) {
    Pageable pageable = PageRequest.of(page - 1, pageSize);
    List<QuizDetailResponse> quizDetailResponseList =
        quizSessionRepository.findAllQuizzSessionByClassId(classRoomId, pageable);
    Page<QuizDetailResponse> quizDetailResponsePage =
        new PageImpl<>(quizDetailResponseList, pageable, quizDetailResponseList.size());
    return PaginationResponse.<QuizDetailResponse>builder()
        .page(quizDetailResponsePage.getNumber())
        .pageSize(quizDetailResponsePage.getSize())
        .pages(quizDetailResponsePage.getTotalPages())
        .total(quizDetailResponsePage.getTotalElements())
        .data(quizDetailResponsePage.getContent())
        .build();
  }

  @Override
  public void inviteStudentsToClassRoom(
      InviteStudentsToClassRoomRequest inviteStudentsToClassRoomRequest) {
    ClassRoom classRoom =
        classRoomRepository
            .findById(inviteStudentsToClassRoomRequest.classRoomId())
            .orElseThrow(() -> new QuizException(ErrorCode.MESSAGE_INVALID_ID));
    User teacher =
        userRepository
            .findById(classRoom.getTeacherId())
            .orElseThrow(() -> new QuizException(ErrorCode.MESSAGE_INVALID_ID));
    String emailList = String.join(",", inviteStudentsToClassRoomRequest.studentEmails());
    String message =
        String.format(
            "email=%s;classCode=%s;teacherName=%s;classroomName=%s",
            emailList,
            classRoom.getClassCode(),
            teacher.getFirstName() + " " + teacher.getLastName(),
            classRoom.getName());
    kafkaTemplate.send("send-class-code-to-emails", message);
    log.info("Invited students to classroom with ID: {}", classRoom.getId());
  }

  @Override
  public void removeStudentFromClassRoom(String classRoomId, String studentId) {
    ClassRoom classRoom =
        classRoomRepository
            .findById(classRoomId)
            .orElseThrow(() -> new QuizException(ErrorCode.MESSAGE_INVALID_ID));
    classRoom.getStudentIds().remove(studentId);
    classRoomRepository.save(classRoom);
    // Remove the classRoomId from the student's classIds
    User student =
        userRepository
            .findById(studentId)
            .orElseThrow(() -> new QuizException(ErrorCode.MESSAGE_INVALID_ID));
    student.getClassIds().remove(classRoomId);
    userRepository.save(student);
  }

  public Boolean joinClassRoom(String classCode) {
    CustomUserDetail userDetail = SecurityUtils.getUserDetail();
    User user = userDetail.getUser();
    ClassRoom classRoom =
        classRoomRepository
            .findByClassCode(classCode)
            .orElseThrow(
                () -> new RuntimeException("Classroom with code " + classCode + " not found"));
    List<String> studentIds = classRoom.getStudentIds();
    if (studentIds.contains(userDetail.getUser().getId())) {
      return false; // Student already in the classroom
    }
    studentIds.add(userDetail.getUser().getId());
    classRoom.setStudentIds(studentIds);

    // Add the classroom ID to the user's classIds
    List<String> classIds = userDetail.getUser().getClassIds();
    if (!classIds.contains(classRoom.getId())) {
      System.out.println("classRoom.getId() = " + classRoom.getId());
      System.out.println("classIds before = " + classIds);
      classIds.add(classRoom.getId());
      user.setClassIds(classIds);
      System.out.println("classIds after = " + user.getClassIds());
      userRepository.save(user);
    }
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
    Quiz quiz =
        quizRepository
            .findById(quizId)
            .orElseThrow(() -> new QuizException(ErrorCode.MESSAGE_INVALID_ID));
    if (!quiz.isActive()) {
      throw new QuizException(ErrorCode.MESSAGE_QUIZ_NOT_ACTIVE);
    }
    assignedQuizIds.add(quizId);
    classRoom.setAssignedQuizIds(assignedQuizIds);
    classRoomRepository.save(classRoom);
    // Import Quiz and add the classRoomId to the quiz's classIds
    List<String> classRoomIds = quiz.getClassIds();
    if (!classRoomIds.contains(classRoomId)) {
      classRoomIds.add(classRoomId);
      quiz.setClassIds(classRoomIds);
      quizRepository.save(quiz);
    }

    return true;
  }
}
