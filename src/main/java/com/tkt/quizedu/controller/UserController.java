package com.tkt.quizedu.controller;

import java.util.concurrent.TimeUnit;

import jakarta.validation.Valid;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.tkt.quizedu.component.Translator;
import com.tkt.quizedu.data.constant.EndpointConstant;
import com.tkt.quizedu.data.constant.ErrorCode;
import com.tkt.quizedu.data.dto.request.*;
import com.tkt.quizedu.data.dto.request.ChangePasswordDTORequest;
import com.tkt.quizedu.data.dto.request.StudentCreationDTORequest;
import com.tkt.quizedu.data.dto.request.TeacherCreationDTORequest;
import com.tkt.quizedu.data.dto.request.UserCreationDTORequest;
import com.tkt.quizedu.data.dto.response.StudentProfileResponse;
import com.tkt.quizedu.data.dto.response.SuccessApiResponse;
import com.tkt.quizedu.data.dto.response.TeacherProfileResponse;
import com.tkt.quizedu.data.dto.response.UserBaseResponse;
import com.tkt.quizedu.service.s3.IS3Service;
import com.tkt.quizedu.service.user.IUserService;
import com.tkt.quizedu.utils.GenerateVerificationCode;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping(EndpointConstant.ENDPOINT_USER)
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Slf4j(topic = "USER-CONTROLLER")
@Tag(name = "User Management", description = "APIs for user registration and management")
public class UserController {

  IUserService userService;
  IS3Service s3Service;
  KafkaTemplate<String, String> kafkaTemplate;
  RedisTemplate<String, Object> redisTemplate;

  @PostMapping("/student")
  @ResponseStatus(HttpStatus.CREATED)
  public SuccessApiResponse<UserBaseResponse> registerStudent(
      @RequestBody @Valid StudentCreationDTORequest req) {
    return handleUserRegistration(req);
  }

  @PostMapping("/teacher")
  @ResponseStatus(HttpStatus.CREATED)
  public SuccessApiResponse<UserBaseResponse> registerTeacher(
      @RequestBody @Valid TeacherCreationDTORequest req) {
    return handleUserRegistration(req);
  }

  private SuccessApiResponse<UserBaseResponse> handleUserRegistration(UserCreationDTORequest req) {
    var userResponse = userService.save(req);
    log.info("User with email {} has been registered successfully", req.getEmail());

    sendVerificationEmail(userResponse, req);

    return SuccessApiResponse.<UserBaseResponse>builder()
        .code(ErrorCode.MESSAGE_SUCCESS.getCode())
        .status(HttpStatus.CREATED.value())
        .message(Translator.toLocale(ErrorCode.MESSAGE_SUCCESS.getCode()))
        .data(userResponse)
        .build();
  }

  private void sendVerificationEmail(UserBaseResponse userResponse, UserCreationDTORequest req) {
    String code = GenerateVerificationCode.generateCode();
    String key = "user:confirmation:" + userResponse.getEmail();

    redisTemplate.opsForValue().set(key, code, 10, TimeUnit.MINUTES);

    String message =
        String.format(
            "email=%s,name=%s,code=%s",
            req.getEmail(), req.getFirstName() + " " + req.getLastName(), code);
    kafkaTemplate.send("confirm-account-topic", message);
    log.info("Confirmation email sent to Kafka topic with message: {}", message);
  }

  @GetMapping("/my-profile")
  SuccessApiResponse<UserBaseResponse> getMyProfile() {
    UserBaseResponse userProfile = userService.getMyProfile();
    if (userProfile instanceof StudentProfileResponse studentProfile) {
      return SuccessApiResponse.<UserBaseResponse>builder()
          .code(ErrorCode.MESSAGE_SUCCESS.getCode())
          .status(HttpStatus.OK.value())
          .message(Translator.toLocale(ErrorCode.MESSAGE_SUCCESS.getCode()))
          .data(studentProfile)
          .build();
    } else if (userProfile instanceof TeacherProfileResponse teacherProfile) {
      return SuccessApiResponse.<UserBaseResponse>builder()
          .code(ErrorCode.MESSAGE_SUCCESS.getCode())
          .status(HttpStatus.OK.value())
          .message(Translator.toLocale(ErrorCode.MESSAGE_SUCCESS.getCode()))
          .data(teacherProfile)
          .build();
    } else {
      return SuccessApiResponse.<UserBaseResponse>builder()
          .code(ErrorCode.MESSAGE_SUCCESS.getCode())
          .status(HttpStatus.OK.value())
          .message(Translator.toLocale(ErrorCode.MESSAGE_SUCCESS.getCode()))
          .data(userProfile)
          .build();
    }
  }

  @PatchMapping("/change-password")
  SuccessApiResponse<Void> changePassword(@Valid @RequestBody ChangePasswordDTORequest req) {
    userService.changePassword(req);
    return SuccessApiResponse.<Void>builder()
        .code(ErrorCode.MESSAGE_SUCCESS.getCode())
        .status(HttpStatus.OK.value())
        .message(Translator.toLocale(ErrorCode.MESSAGE_SUCCESS.getCode()))
        .build();
  }

  @PutMapping("/student/profile")
  SuccessApiResponse<UserBaseResponse> updateProfile(
      @RequestPart(name = "avatar", required = false) MultipartFile avatar,
      @Valid @ModelAttribute StudentUpdateRequest req) {
    return updateProfile(req, avatar);
  }

  @PutMapping("/teacher/profile")
  SuccessApiResponse<UserBaseResponse> updateTeacherProfile(
      @RequestPart(name = "avatar", required = false) MultipartFile avatar,
      @Valid @ModelAttribute TeacherUpdateRequest req) {

    return updateProfile(req, avatar);
  }

  private SuccessApiResponse<UserBaseResponse> updateProfile(
      UserUpdateDTORequest req, MultipartFile avatar) {

    UserBaseResponse updatedProfile = userService.updateProfile(req, avatar);

    if (updatedProfile instanceof StudentProfileResponse studentProfile) {
      return SuccessApiResponse.<UserBaseResponse>builder()
          .code(ErrorCode.MESSAGE_SUCCESS.getCode())
          .status(HttpStatus.OK.value())
          .message(Translator.toLocale(ErrorCode.MESSAGE_SUCCESS.getCode()))
          .data(studentProfile)
          .build();
    } else if (updatedProfile instanceof TeacherProfileResponse teacherProfile) {
      return SuccessApiResponse.<UserBaseResponse>builder()
          .code(ErrorCode.MESSAGE_SUCCESS.getCode())
          .status(HttpStatus.OK.value())
          .message(Translator.toLocale(ErrorCode.MESSAGE_SUCCESS.getCode()))
          .data(teacherProfile)
          .build();
    }
    return SuccessApiResponse.<UserBaseResponse>builder()
        .code(ErrorCode.MESSAGE_SUCCESS.getCode())
        .status(HttpStatus.OK.value())
        .message(Translator.toLocale(ErrorCode.MESSAGE_SUCCESS.getCode()))
        .data(updatedProfile)
        .build();
  }
}
