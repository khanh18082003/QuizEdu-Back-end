package com.tkt.quizedu.data.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StudentUpdateResponse extends UserUpdateResponse{
    String level;
    String schoolName;

    public StudentUpdateResponse(String email, String firstName, String lastName, String displayName, String level, String schoolName) {
        super(email, firstName, lastName, displayName);
        this.level = level;
        this.schoolName = schoolName;
    }
}
