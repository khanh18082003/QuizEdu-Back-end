package com.tkt.quizedu.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import com.tkt.quizedu.component.Translator;
import com.tkt.quizedu.data.constant.EndpointConstant;
import com.tkt.quizedu.data.constant.ErrorCode;
import com.tkt.quizedu.data.dto.request.AssignQuizToClassroomRequest;
import com.tkt.quizedu.data.dto.request.ClassRoomRequest;
import com.tkt.quizedu.data.dto.request.InviteStudentsToClassRoomRequest;
import com.tkt.quizedu.data.dto.request.JoinClassRoomRequest;
import com.tkt.quizedu.data.dto.response.*;
import com.tkt.quizedu.service.classroom.IClassRoomService;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping(EndpointConstant.ENDPOINT_CLASSROOM)
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Slf4j(topic = "CLASSROOM-CONTROLLER")
@Tag(name = "ClassRoom Management", description = "APIs for classRoom registration and management")
public class ClassRoomController {
  IClassRoomService classRoomService;

  @PostMapping
  public SuccessApiResponse<ClassRoomResponse> createClassRoom(
      @RequestBody ClassRoomRequest classRoomRequest) {
    ClassRoomResponse response = classRoomService.createClassRoom(classRoomRequest);

    return SuccessApiResponse.<ClassRoomResponse>builder()
        .code(ErrorCode.MESSAGE_SUCCESS.getCode())
        .status(HttpStatus.CREATED.value())
        .message(Translator.toLocale(ErrorCode.MESSAGE_SUCCESS.getCode()))
        .data(response)
        .build();
  }

  @PostMapping("/invited-students")
  SuccessApiResponse<Void> inviteStudentsToClassRoom(
      @RequestBody InviteStudentsToClassRoomRequest inviteStudentsToClassRoomRequest) {
    classRoomService.inviteStudentsToClassRoom(inviteStudentsToClassRoomRequest);
    return SuccessApiResponse.<Void>builder()
        .code(ErrorCode.MESSAGE_SUCCESS.getCode())
        .status(HttpStatus.OK.value())
        .message(Translator.toLocale(ErrorCode.MESSAGE_SUCCESS.getCode()))
        .build();
  }

  @PutMapping("/{classRoomId}")
  public SuccessApiResponse<ClassRoomResponse> updateClassRoom(
      @PathVariable String classRoomId, @RequestBody ClassRoomRequest classRoomRequest) {
    ClassRoomResponse response = classRoomService.updateClassRoom(classRoomId, classRoomRequest);

    return SuccessApiResponse.<ClassRoomResponse>builder()
        .code(ErrorCode.MESSAGE_SUCCESS.getCode())
        .status(HttpStatus.OK.value())
        .message(Translator.toLocale(ErrorCode.MESSAGE_SUCCESS.getCode()))
        .data(response)
        .build();
  }

  @DeleteMapping("/{classRoomId}")
  public SuccessApiResponse<Void> deleteClassRoom(@PathVariable String classRoomId) {
    classRoomService.deleteClassRoom(classRoomId);
    return SuccessApiResponse.<Void>builder()
        .code(ErrorCode.MESSAGE_SUCCESS.getCode())
        .status(HttpStatus.OK.value())
        .message(Translator.toLocale(ErrorCode.MESSAGE_SUCCESS.getCode()))
        .build();
  }

  @PostMapping("/joinClassRoom")
  public SuccessApiResponse<Boolean> joinClassRoom(@RequestBody JoinClassRoomRequest classCode) {
    Boolean response = classRoomService.joinClassRoom(classCode.classCode());
    return SuccessApiResponse.<Boolean>builder()
        .code(ErrorCode.MESSAGE_SUCCESS.getCode())
        .status(HttpStatus.OK.value())
        .message(Translator.toLocale(ErrorCode.MESSAGE_SUCCESS.getCode()))
        .data(response)
        .build();
  }

  @PostMapping("/assignQuizToClassroom")
  public SuccessApiResponse<Boolean> assignQuizToClassroom(
      @RequestBody AssignQuizToClassroomRequest assignQuizToClassroomRequest) {
    Boolean response =
        classRoomService.assignQuizToClassroom(
            assignQuizToClassroomRequest.classRoomId(), assignQuizToClassroomRequest.quizId());
    return SuccessApiResponse.<Boolean>builder()
        .code(ErrorCode.MESSAGE_SUCCESS.getCode())
        .status(HttpStatus.OK.value())
        .message(Translator.toLocale(ErrorCode.MESSAGE_SUCCESS.getCode()))
        .data(response)
        .build();
  }

  @GetMapping("/{classRoomId}")
  public SuccessApiResponse<ClassroomDetailResponse> getClassroomDetail(
      @PathVariable String classRoomId) {

    ClassroomDetailResponse response = classRoomService.getClassroomDetailById(classRoomId);
    return SuccessApiResponse.<ClassroomDetailResponse>builder()
        .code(ErrorCode.MESSAGE_SUCCESS.getCode())
        .status(HttpStatus.OK.value())
        .message(Translator.toLocale(ErrorCode.MESSAGE_SUCCESS.getCode()))
        .data(response)
        .build();
  }

  @GetMapping("/{classRoomId}/students/all")
  SuccessApiResponse<PaginationResponse<UserBaseResponse>> getAllStudentsInClassRoom(
      @PathVariable String classRoomId,
      @RequestParam(name = "page", defaultValue = "1") int page,
      @RequestParam(name = "pageSize", defaultValue = "20") int pageSize) {
    return SuccessApiResponse.<PaginationResponse<UserBaseResponse>>builder()
        .code(ErrorCode.MESSAGE_SUCCESS.getCode())
        .status(HttpStatus.OK.value())
        .message(Translator.toLocale(ErrorCode.MESSAGE_SUCCESS.getCode()))
        .data(classRoomService.getAllStudentsInClassRoom(classRoomId, page, pageSize))
        .build();
  }

  @GetMapping("/{classRoomId}/quizSessions")
  SuccessApiResponse<PaginationResponse<QuizDetailResponse>> getQuizSessionsByClassRoomId(
      @PathVariable String classRoomId,
      @RequestParam(name = "page", defaultValue = "1") int page,
      @RequestParam(name = "pageSize", defaultValue = "3") int pageSize) {
    return SuccessApiResponse.<PaginationResponse<QuizDetailResponse>>builder()
        .code(ErrorCode.MESSAGE_SUCCESS.getCode())
        .status(HttpStatus.OK.value())
        .message(Translator.toLocale(ErrorCode.MESSAGE_SUCCESS.getCode()))
        .data(classRoomService.getQuizSessionsByClassRoomId(classRoomId, page, pageSize))
        .build();
  }

  @DeleteMapping("/{classRoomId}/students/{studentId}")
  SuccessApiResponse<Void> removeStudentFromClassRoom(
      @PathVariable String classRoomId, @PathVariable String studentId) {
    classRoomService.removeStudentFromClassRoom(classRoomId, studentId);
    return SuccessApiResponse.<Void>builder()
        .code(ErrorCode.MESSAGE_SUCCESS.getCode())
        .status(HttpStatus.OK.value())
        .message(Translator.toLocale(ErrorCode.MESSAGE_SUCCESS.getCode()))
        .build();
  }
}
