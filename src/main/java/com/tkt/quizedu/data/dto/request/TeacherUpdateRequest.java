package com.tkt.quizedu.data.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TeacherUpdateRequest extends UserUpdateRequest{
    Set<String> subjects;
    String experience;
    String schoolName;

    public TeacherUpdateRequest(String firstName, String lastName, String displayName, Set<String> subjects, String experience, String schoolName) {
        super(firstName, lastName, displayName);
        this.subjects = subjects;
        this.experience = experience;
        this.schoolName = schoolName;
    }
}
