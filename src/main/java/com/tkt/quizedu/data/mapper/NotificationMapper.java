package com.tkt.quizedu.data.mapper;

import org.mapstruct.Mapper;

import com.tkt.quizedu.data.collection.Notification;
import com.tkt.quizedu.data.dto.request.NotificationRequest;

@Mapper(componentModel = "spring")
public interface NotificationMapper {
  Notification toNotification(NotificationRequest request);
}
