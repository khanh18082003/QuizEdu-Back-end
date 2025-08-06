package com.tkt.quizedu.controller;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.tkt.quizedu.component.Translator;
import com.tkt.quizedu.data.constant.EndpointConstant;
import com.tkt.quizedu.data.constant.ErrorCode;
import com.tkt.quizedu.data.dto.request.CommentRequest;
import com.tkt.quizedu.data.dto.request.NotificationRequest;
import com.tkt.quizedu.data.dto.response.CommentResponse;
import com.tkt.quizedu.data.dto.response.NotificationResponse;
import com.tkt.quizedu.data.dto.response.SuccessApiResponse;
import com.tkt.quizedu.service.notification.INotificationService;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping(EndpointConstant.ENDPOINT_NOTIFICATION)
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Slf4j(topic = "NOTIFICATION-CONTROLLER")
@Tag(
    name = "Notification Management",
    description = "APIs for notification registration and management")
public class NotificationController {
  INotificationService notificationService;

  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @ResponseStatus(HttpStatus.CREATED)
  SuccessApiResponse<NotificationResponse> save(
      @RequestPart(required = false) MultipartFile[] files,
      @Valid @ModelAttribute NotificationRequest request) {
    return SuccessApiResponse.<NotificationResponse>builder()
        .code(ErrorCode.MESSAGE_SUCCESS.getCode())
        .status(HttpStatus.CREATED.value())
        .message(Translator.toLocale(ErrorCode.MESSAGE_SUCCESS.getCode()))
        .data(notificationService.save(request, files))
        .build();
  }

  @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @ResponseStatus(HttpStatus.OK)
  SuccessApiResponse<NotificationResponse> update(
      @PathVariable String id,
      @RequestPart MultipartFile[] files,
      @ModelAttribute NotificationRequest request) {
    return SuccessApiResponse.<NotificationResponse>builder()
        .code(ErrorCode.MESSAGE_SUCCESS.getCode())
        .status(HttpStatus.OK.value())
        .message(Translator.toLocale(ErrorCode.MESSAGE_SUCCESS.getCode()))
        .data(notificationService.update(id, request, files))
        .build();
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.OK)
  SuccessApiResponse<Void> delete(@PathVariable String id) {
    notificationService.delete(id);
    return SuccessApiResponse.<Void>builder()
        .code(ErrorCode.MESSAGE_SUCCESS.getCode())
        .status(HttpStatus.NO_CONTENT.value())
        .message(Translator.toLocale(ErrorCode.MESSAGE_SUCCESS.getCode()))
        .build();
  }

  @GetMapping("/getAll")
  @ResponseStatus(HttpStatus.OK)
  SuccessApiResponse<List<NotificationResponse>> getAll(@RequestParam("class_id") String classId) {
    return SuccessApiResponse.<List<NotificationResponse>>builder()
        .code(ErrorCode.MESSAGE_SUCCESS.getCode())
        .status(HttpStatus.OK.value())
        .message(Translator.toLocale(ErrorCode.MESSAGE_SUCCESS.getCode()))
        .data(notificationService.getAllByClassId(classId))
        .build();
  }

  @PostMapping("/{notificationId}/comment")
  @ResponseStatus(HttpStatus.CREATED)
  SuccessApiResponse<CommentResponse> addComment(
      @PathVariable String notificationId, @RequestBody CommentRequest comment) {
    return SuccessApiResponse.<CommentResponse>builder()
        .code(ErrorCode.MESSAGE_SUCCESS.getCode())
        .status(HttpStatus.CREATED.value())
        .message(Translator.toLocale(ErrorCode.MESSAGE_SUCCESS.getCode()))
        .data(notificationService.addComment(notificationId, comment.content()))
        .build();
  }

  @DeleteMapping("/{notificationId}/comment/{commentId}")
  @ResponseStatus(HttpStatus.OK)
  SuccessApiResponse<Void> deleteComment(
      @PathVariable String notificationId, @PathVariable String commentId) {
    notificationService.deleteComment(notificationId, commentId);
    return SuccessApiResponse.<Void>builder()
        .code(ErrorCode.MESSAGE_SUCCESS.getCode())
        .status(HttpStatus.NO_CONTENT.value())
        .message(Translator.toLocale(ErrorCode.MESSAGE_SUCCESS.getCode()))
        .build();
  }

  @PutMapping("/{notificationId}/comment/{commentId}")
  @ResponseStatus(HttpStatus.OK)
  SuccessApiResponse<CommentResponse> updateComment(
      @PathVariable String notificationId,
      @PathVariable String commentId,
      @RequestBody CommentRequest comment) {
    return SuccessApiResponse.<CommentResponse>builder()
        .code(ErrorCode.MESSAGE_SUCCESS.getCode())
        .status(HttpStatus.OK.value())
        .message(Translator.toLocale(ErrorCode.MESSAGE_SUCCESS.getCode()))
        .data(notificationService.updateComment(notificationId, commentId, comment.content()))
        .build();
  }
}
