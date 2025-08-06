package com.tkt.quizedu.service.notification;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.tkt.quizedu.data.collection.Notification;
import com.tkt.quizedu.data.collection.User;
import com.tkt.quizedu.data.dto.request.NotificationRequest;
import com.tkt.quizedu.data.dto.response.CommentResponse;
import com.tkt.quizedu.data.dto.response.NotificationResponse;
import com.tkt.quizedu.data.dto.response.UserBaseResponse;
import com.tkt.quizedu.data.mapper.NotificationMapper;
import com.tkt.quizedu.data.repository.ClassRoomRepository;
import com.tkt.quizedu.data.repository.NotificationRepository;
import com.tkt.quizedu.data.repository.UserRepository;
import com.tkt.quizedu.service.s3.IS3Service;
import com.tkt.quizedu.utils.SecurityUtils;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import static org.apache.kafka.common.serialization.Serdes.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Slf4j(topic = "QUIZ-SERVICE")
public class NotificationServiceImpl implements INotificationService {
  NotificationMapper notificationMapper;
  NotificationRepository notificationRepository;
  UserRepository userRepository;
  ClassRoomRepository classRoomRepository;
  IS3Service s3Service;

  @Override
  @Transactional
  public NotificationResponse save(NotificationRequest request, MultipartFile[] files) {
    Notification notification = notificationMapper.toNotification(request);
    if (files != null) {
      for (MultipartFile file : files) {
        if (file != null && !file.isEmpty()) {
          String xpath = s3Service.uploadFile(file);
          notification.getXPathFiles().add(xpath);
        }
      }
    }
    notification.setTeacherId(SecurityUtils.getUserDetail().getUser().getId());
    notificationRepository.save(notification);
    User teacher =
        userRepository
            .findById(notification.getTeacherId())
            .orElseThrow(
                () ->
                    new IllegalArgumentException(
                        "Teacher not found with id: " + notification.getTeacherId()));
    UserBaseResponse userBaseResponse =
        UserBaseResponse.builder()
            .id(teacher.getId())
            .email(teacher.getEmail())
            .firstName(teacher.getFirstName())
            .lastName(teacher.getLastName())
            .avatar(teacher.getAvatar())
            .displayName(teacher.getDisplayName())
            .build();
    return NotificationResponse.builder()
        .id(notification.getId())
        .description(notification.getDescription())
        .classRoom(classRoomRepository.findById(notification.getClassId()).orElse(null))
        .teacher(userBaseResponse)
        .xPathFiles(
            notification.getXPathFiles() != null ? notification.getXPathFiles() : new ArrayList<>())
        .createdAt(notification.getCreatedAt())
        .updatedAt(notification.getUpdatedAt())
        .build();
  }

  @Override
  public NotificationResponse getById(String id) {
    String userId = SecurityUtils.getUserDetail().getUser().getId();
    User teacher =
        userRepository
            .findById(userId)
            .orElseThrow(
                () -> new IllegalArgumentException("Teacher not found with id: " + userId));
    UserBaseResponse userBaseResponse =
        UserBaseResponse.builder()
            .id(teacher.getId())
            .email(teacher.getEmail())
            .firstName(teacher.getFirstName())
            .lastName(teacher.getLastName())
            .avatar(teacher.getAvatar())
            .displayName(teacher.getDisplayName())
            .build();
    return notificationRepository
        .findById(id)
        .map(
            notification ->
                NotificationResponse.builder()
                    .id(notification.getId())
                    .description(notification.getDescription())
                    .classRoom(classRoomRepository.findById(notification.getClassId()).orElse(null))
                    .teacher(userBaseResponse)
                    .xPathFiles(
                        notification.getXPathFiles() != null
                            ? notification.getXPathFiles()
                            : new ArrayList<>())
                    .createdAt(notification.getCreatedAt())
                    .updatedAt(notification.getUpdatedAt())
                    .build())
        .orElse(null);
  }

  @Override
  public void delete(String id) {
    Notification notification =
        notificationRepository
            .findById(id)
            .orElseThrow(
                () -> new IllegalArgumentException("Notification not found with id: " + id));
    if (notification.getXPathFiles() != null) {
      for (String xpath : notification.getXPathFiles()) {
        s3Service.deleteFile(xpath);
      }
    }
    notificationRepository.delete(notification);
  }

  @Override
  public NotificationResponse update(
      String id, NotificationRequest request, MultipartFile[] files) {
    Notification notification =
        notificationRepository
            .findById(id)
            .orElseThrow(
                () -> new IllegalArgumentException("Notification not found with id: " + id));

    notification.setDescription(request.description());
    if (notification.getXPathFiles() != null) {
      for (String xpath : notification.getXPathFiles()) {
        s3Service.deleteFile(xpath);
      }
      notification.getXPathFiles().clear();
    }
    for (MultipartFile file : files) {
      if (file != null && !file.isEmpty()) {
        String xpath = s3Service.uploadFile(file);
        notification.getXPathFiles().add(xpath);
      }
    }
    notificationRepository.save(notification);

    User teacher =
        userRepository
            .findById(notification.getTeacherId())
            .orElseThrow(
                () ->
                    new IllegalArgumentException(
                        "Teacher not found with id: " + notification.getTeacherId()));
    UserBaseResponse userBaseResponse =
        UserBaseResponse.builder()
            .id(teacher.getId())
            .email(teacher.getEmail())
            .firstName(teacher.getFirstName())
            .lastName(teacher.getLastName())
            .avatar(teacher.getAvatar())
            .displayName(teacher.getDisplayName())
            .build();
    // Builder response
    return NotificationResponse.builder()
        .id(notification.getId())
        .description(notification.getDescription())
        .classRoom(classRoomRepository.findById(notification.getClassId()).orElse(null))
        .teacher(userBaseResponse)
        .xPathFiles(
            notification.getXPathFiles() != null ? notification.getXPathFiles() : new ArrayList<>())
        .createdAt(notification.getCreatedAt())
        .updatedAt(notification.getUpdatedAt())
        .build();
  }

  @Override
  public List<NotificationResponse> getAllByClassId(String classId) {
    String userId = SecurityUtils.getUserDetail().getUser().getId();
    User teacher =
        userRepository
            .findById(userId)
            .orElseThrow(
                () -> new IllegalArgumentException("Teacher not found with id: " + userId));
    UserBaseResponse userBaseResponse =
        UserBaseResponse.builder()
            .id(teacher.getId())
            .email(teacher.getEmail())
            .firstName(teacher.getFirstName())
            .lastName(teacher.getLastName())
            .avatar(teacher.getAvatar())
            .displayName(teacher.getDisplayName())
            .build();
      System.out.println(notificationRepository.findAllByClassId(classId).size());
    return notificationRepository.findAllByClassId(classId).stream()
        .map(
            notification ->
                NotificationResponse.builder()
                    .id(notification.getId())
                    .description(notification.getDescription())
                    .classRoom(classRoomRepository.findById(notification.getClassId()).orElse(null))
                    .teacher(userBaseResponse)
                    .xPathFiles(
                        notification.getXPathFiles() != null
                            ? notification.getXPathFiles()
                            : new ArrayList<>())
                    .createdAt(notification.getCreatedAt())
                    .updatedAt(notification.getUpdatedAt())
                        .comments(
                            notification.getComments() != null
                                ? notification.getComments().stream()
                                    .map(comment -> {
                                        User user = userRepository.findById(comment.getUserId())
                                                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + comment.getUserId()));

                                        UserBaseResponse userComment = UserBaseResponse.builder()
                                                .id(user.getId())
                                                .email(user.getEmail())
                                                .firstName(user.getFirstName())
                                                .lastName(user.getLastName())
                                                .avatar(user.getAvatar())
                                                .displayName(user.getDisplayName())
                                                .build();

                                        return CommentResponse.builder()
                                                .id(comment.getId().toString())
                                                .content(comment.getContent())
                                                .createdAt(comment.getCreatedAt())
                                                .updatedAt(comment.getUpdatedAt())
                                                .user(userComment)
                                                .build();
                                    })
                                    .toList()
                                : new ArrayList<>())
                    .build())
        .toList();
  }

  @Override
  public CommentResponse addComment(String notificationId, String comment) {
    Notification notification =
        notificationRepository
            .findById(notificationId)
            .orElseThrow(
                () ->
                    new IllegalArgumentException(
                        "Notification not found with id: " + notificationId));
    String userId = SecurityUtils.getUserDetail().getUser().getId();
    Notification.Comment newComment = new Notification.Comment(userId, comment);
    notification.getComments().add(newComment);
    notificationRepository.save(notification);
    User user =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));
      UserBaseResponse userBaseResponse =
              UserBaseResponse.builder()
                      .id(user.getId())
                      .email(user.getEmail())
                      .firstName(user.getFirstName())
                      .lastName(user.getLastName())
                      .avatar(user.getAvatar())
                      .displayName(user.getDisplayName())
                      .build();
    return CommentResponse.builder()
        .id(newComment.getId().toString())
        .user(userBaseResponse)
        .content(newComment.getContent())
        .createdAt(newComment.getCreatedAt())
        .updatedAt(newComment.getUpdatedAt())
        .build();
  }

  @Override
  public void deleteComment(String notificationId, String commentId) {
    Notification notification =
        notificationRepository
            .findById(notificationId)
            .orElseThrow(
                () ->
                    new IllegalArgumentException(
                        "Notification not found with id: " + notificationId));

    notification.getComments().removeIf(comment -> comment.getId().equals(UUID.fromString(commentId)));
    notificationRepository.save(notification);
  }

  @Override
  public CommentResponse updateComment(String notificationId, String commentId, String comment) {
    Notification notification =
        notificationRepository
            .findById(notificationId)
            .orElseThrow(
                () ->
                    new IllegalArgumentException(
                        "Notification not found with id: " + notificationId));
    Notification.Comment existingComment =
        notification.getComments().stream()
            .filter(c -> c.getId().equals(UUID.fromString(commentId)))
            .findFirst()
            .orElseThrow(
                () -> new IllegalArgumentException("Comment not found with id: " + commentId));
    existingComment.setContent(comment);
    existingComment.setUpdatedAt(LocalDateTime.now());
    notificationRepository.save(notification);
    User user =
        userRepository
            .findById(existingComment.getUserId())
            .orElseThrow(
                () -> new IllegalArgumentException("User not found with id: " + existingComment.getUserId()));
      UserBaseResponse userBaseResponse =
              UserBaseResponse.builder()
                      .id(user.getId())
                      .email(user.getEmail())
                      .firstName(user.getFirstName())
                      .lastName(user.getLastName())
                      .avatar(user.getAvatar())
                      .displayName(user.getDisplayName())
                      .build();
    return CommentResponse.builder()
        .id(existingComment.getId().toString())
        .user(userBaseResponse)
        .content(existingComment.getContent())
        .createdAt(existingComment.getCreatedAt())
        .updatedAt(existingComment.getUpdatedAt())
        .build();
  }
}
