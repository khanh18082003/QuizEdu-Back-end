package com.tkt.quizedu.data.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TeacherUpdateResponse extends UserUpdateResponse {
    Set<String> subjects;
    String experience;
    String schoolName;

    public TeacherUpdateResponse(String email, String firstName, String lastName, String displayName, Set<String> subjects, String experience, String schoolName) {
        super(email, firstName, lastName, displayName);
        this.subjects = subjects;
        this.experience = experience;
        this.schoolName = schoolName;
    }
}
