package com.tkt.quizedu.data.collection;

import com.tkt.quizedu.data.base.StringIdentityCollection;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serial;
import java.time.LocalDate;
import java.util.List;

@Document(collection = "classRooms")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class ClassRoom extends StringIdentityCollection {
    @Serial
    private static final long serialVersionUID = -5465733518693373245L;
    @Id
    String id;
    String name;
    String description;
    @Indexed(unique = true)
    String teacherId;
    @Indexed(unique = true)
    String classCode;
    List<String> studentIds;
    List<String> assignedGameIds;
    LocalDate createdAt;
    boolean  isActive;
}
