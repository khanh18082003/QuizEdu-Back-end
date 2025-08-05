package com.tkt.quizedu.data.dto.request;

public record UpdateQuizRequest(
        String name,
        String description,
        boolean isActive,
        boolean isPublic
) {
}
