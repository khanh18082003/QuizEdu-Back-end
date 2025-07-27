package com.tkt.quizedu.data.collection;

import java.io.Serial;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

import com.tkt.quizedu.data.base.StringIdentityCollection;
import com.tkt.quizedu.data.constant.EducationLevel;
import com.tkt.quizedu.data.constant.UserRole;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Document(collection = "users")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class User extends StringIdentityCollection {
  @Serial private static final long serialVersionUID = -5465733518693373245L;

  @Id
  @Field(name = "_id", targetType = FieldType.OBJECT_ID)
  String id;

  @Indexed(unique = true)
  String email;

  String password;

  String firstName;

  String lastName;

  String displayName;

  String avatar;

  UserRole role;

  EducationLevel level;

  String schoolName;

  boolean isActive;

  @Builder.Default List<String> classIds = new ArrayList<>();

  Set<String> subjects;

  String experience;

  String qualification;

  @CreatedDate LocalDateTime createdAt;

  @LastModifiedDate LocalDateTime updatedAt;
}
