package com.tkt.quizedu.data.collection;

import java.io.Serial;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

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
  LocalDate createdAt;
  boolean isActive;
}
