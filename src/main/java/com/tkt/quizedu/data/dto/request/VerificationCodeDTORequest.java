package com.tkt.quizedu.data.dto.request;

import jakarta.validation.constraints.NotBlank;

import java.io.Serializable;

public record VerificationCodeDTORequest(
        @NotBlank(message = "MESSAGE_NOT_BLANK")
        String userId,
        @NotBlank(message = "MESSAGE_NOT_BLANK")
        String code
) implements Serializable {
}
