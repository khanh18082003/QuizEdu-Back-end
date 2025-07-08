package com.tkt.quizedu.data.dto.response;

import com.tkt.quizedu.data.constant.UserRole;
import lombok.Builder;

import java.io.Serializable;
import java.time.LocalDateTime;

@Builder
public record UserBaseResponse(
        String id,
        String email,
        String firstName,
        String lastName,
        String displayName,
        String avatar,
        boolean isActive,
        UserRole role,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) implements Serializable {
}
