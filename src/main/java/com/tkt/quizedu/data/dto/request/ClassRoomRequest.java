package com.tkt.quizedu.data.dto.request;

import java.time.LocalDate;

public record ClassRoomRequest (
        String name,
        String description,
        String teacherId,
        String classCode,
        boolean isActive
)
implements java.io.Serializable {
}
