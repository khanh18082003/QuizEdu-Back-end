package com.tkt.quizedu.service.notification;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.tkt.quizedu.data.dto.request.NotificationRequest;
import com.tkt.quizedu.data.dto.response.CommentResponse;
import com.tkt.quizedu.data.dto.response.NotificationResponse;

public interface INotificationService {
  NotificationResponse save(NotificationRequest request, MultipartFile[] files);

  NotificationResponse getById(String id);

  void delete(String id);

  NotificationResponse update(String id, NotificationRequest request, MultipartFile[] files);

  List<NotificationResponse> getAllByClassId(String classId);

  CommentResponse addComment(String notificationId, String comment);

  void deleteComment(String notificationId, String commentId);

  CommentResponse updateComment(String notificationId, String commentId, String comment);
}
