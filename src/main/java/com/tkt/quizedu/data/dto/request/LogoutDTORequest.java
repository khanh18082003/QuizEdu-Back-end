package com.tkt.quizedu.data.dto.request;

import jakarta.validation.constraints.NotBlank;

public record LogoutDTORequest(@NotBlank(message = "MESSAGE_NOT_BLANK") String role) {}
