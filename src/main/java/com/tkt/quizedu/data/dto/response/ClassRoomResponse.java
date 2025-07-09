package com.tkt.quizedu.data.dto.response;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ClassRoomResponse implements java.io.Serializable {
    @JsonProperty("id")
    String id;
    @JsonProperty("name")
    String name;
    @JsonProperty("description")
    String description;
    @JsonProperty("teacher_id")
    String teacherId;
    @JsonProperty("class_code")
    String classCode;
    @JsonProperty("student_ids")
    java.util.List<String> studentIds;
    @JsonProperty("assigned_game_ids")
    java.util.List<String> assignedGameIds;
    @JsonProperty("created_at")
    java.time.LocalDate createdAt;
    @JsonProperty("is_active")
    boolean isActive;
}
