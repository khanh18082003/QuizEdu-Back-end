package com.tkt.quizedu.controller;

import com.tkt.quizedu.data.dto.request.JoinClassRoomRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

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
    public SuccessApiResponse<Boolean> joinClassRoom(
            @RequestBody JoinClassRoomRequest joinClassRoomRequest) {
        Boolean response = classRoomService.joinClassRoom(joinClassRoomRequest.classRoomId(),
                joinClassRoomRequest.studentId());
        return SuccessApiResponse.<Boolean>builder()
                .code(ErrorCode.MESSAGE_SUCCESS.getCode())
                .status(HttpStatus.OK.value())
                .message(Translator.toLocale(ErrorCode.MESSAGE_SUCCESS.getCode()))
                .data(response)
                .build();
    }

    @PostMapping("/assignQuizToClassroom")
    public SuccessApiResponse<Boolean> assignQuizToClassroom(
            @RequestBody JoinClassRoomRequest joinClassRoomRequest) {
        Boolean response = classRoomService.assignQuizToClassroom(
                joinClassRoomRequest.classRoomId(), joinClassRoomRequest.studentId());
        return SuccessApiResponse.<Boolean>builder()
                .code(ErrorCode.MESSAGE_SUCCESS.getCode())
                .status(HttpStatus.OK.value())
                .message(Translator.toLocale(ErrorCode.MESSAGE_SUCCESS.getCode()))
                .data(response)
                .build();
    }
}
