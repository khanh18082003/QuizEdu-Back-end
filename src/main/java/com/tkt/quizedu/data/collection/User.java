package com.tkt.quizedu.data.collection;

import java.io.Serial;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

import com.tkt.quizedu.data.base.StringIdentityCollection;
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

  @Field(name = "email")
  @Indexed(unique = true)
  String email;

  String password;

  @Field(name = "first_name")
  String firstName;

  @Field(name = "last_name")
  String lastName;

  @Field(name = "display_name")
  String displayName;

  @Field(name = "avatar")
  String avatar;

  UserRole role;

  @Field(name = "is_active")
  boolean isActive;

  @Field(name = "class_ids", targetType = FieldType.ARRAY)
  List<String> classIds;

  @Field(name = "created_at")
  @CreatedDate
  LocalDateTime createdAt;

  @Field(name = "updated_at")
  @LastModifiedDate
  LocalDateTime updatedAt;
}
