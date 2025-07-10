package com.tkt.quizedu.data.dto.request;

import java.io.Serializable;

import jakarta.validation.constraints.NotBlank;

public record VerificationCodeDTORequest(
    @NotBlank(message = "MESSAGE_NOT_BLANK") String userId,
    @NotBlank(message = "MESSAGE_NOT_BLANK") String code)
    implements Serializable {}
