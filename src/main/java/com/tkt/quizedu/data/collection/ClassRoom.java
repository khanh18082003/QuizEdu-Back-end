package com.tkt.quizedu.data.collection;

import java.io.Serial;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.tkt.quizedu.data.base.StringIdentityCollection;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Document(collection = "classRooms")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class ClassRoom extends StringIdentityCollection {
  @Serial private static final long serialVersionUID = -5465733518693373245L;
  @Id String id;
  String name;
  String description;
  String teacherId;

  @Indexed(unique = true)
  String classCode;

  @Builder.Default List<String> studentIds = new ArrayList<>();
  @Builder.Default List<String> assignedQuizIds = new ArrayList<>();

  @Field(name = "created_at")
  @CreatedDate
  LocalDateTime createdAt;

  @Field(name = "updated_at")
  @LastModifiedDate
  LocalDateTime updatedAt;

  boolean isActive;
}
