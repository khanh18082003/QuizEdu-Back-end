package com.tkt.quizedu.data.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.tkt.quizedu.data.base.BaseRepository;
import com.tkt.quizedu.data.collection.Notification;

@Repository
public interface NotificationRepository extends BaseRepository<Notification, String> {
  List<Notification> findAllByClassId(String classId);

  Optional<Notification> findByClassId(String classId);
}
