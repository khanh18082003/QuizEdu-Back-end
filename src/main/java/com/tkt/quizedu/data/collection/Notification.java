package com.tkt.quizedu.data.collection;

import java.io.Serial;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import com.tkt.quizedu.data.base.StringIdentityCollection;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Document(collection = "notifications")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class Notification extends StringIdentityCollection {
  @Serial private static final long serialVersionUID = -5465733518693373245L;

  @Id String id;
  String description;
  String teacherId;
  @Builder.Default List<String> xPathFiles = new ArrayList<>();
  String classId;
  @CreatedDate LocalDateTime createdAt;
  @LastModifiedDate LocalDateTime updatedAt;
  @Builder.Default List<Comment> comments = new ArrayList<>();

  @Data
  public static class Comment {
    UUID id;
    String userId;
    String content;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;

    public Comment(String userId, String content) {
      this.id = UUID.randomUUID();
      this.userId = userId;
      this.content = content;
      this.createdAt = LocalDateTime.now();
    }
  }
}
