package com.tkt.quizedu.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tkt.quizedu.component.Translator;
import com.tkt.quizedu.data.constant.EndpointConstant;
import com.tkt.quizedu.data.constant.ErrorCode;
import com.tkt.quizedu.data.dto.request.ClassRoomRequest;
import com.tkt.quizedu.data.dto.response.ClassRoomResponse;
import com.tkt.quizedu.data.dto.response.SuccessApiResponse;
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

  @PostMapping("/joinClassRoom")
  public SuccessApiResponse<Boolean> joinClassRoom(
      @RequestBody String classRoomId, @RequestBody String studentId) {
    Boolean response = classRoomService.joinClassRoom(classRoomId, studentId);
    return SuccessApiResponse.<Boolean>builder()
        .code(ErrorCode.MESSAGE_SUCCESS.getCode())
        .status(HttpStatus.OK.value())
        .message(Translator.toLocale(ErrorCode.MESSAGE_SUCCESS.getCode()))
        .data(response)
        .build();
  }

  @PostMapping("/assignQuizToClassroom")
  public SuccessApiResponse<Boolean> assignQuizToClassroom(
      @RequestBody String classRoomId, @RequestBody String quizId) {
    Boolean response = classRoomService.assignQuizToClassroom(classRoomId, quizId);
    return SuccessApiResponse.<Boolean>builder()
        .code(ErrorCode.MESSAGE_SUCCESS.getCode())
        .status(HttpStatus.OK.value())
        .message(Translator.toLocale(ErrorCode.MESSAGE_SUCCESS.getCode()))
        .data(response)
        .build();
  }
}
