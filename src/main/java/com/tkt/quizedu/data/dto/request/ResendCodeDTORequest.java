package com.tkt.quizedu.data.dto.request;

import java.io.Serializable;

import jakarta.validation.constraints.NotBlank;

import lombok.Builder;

@Builder
public record ResendCodeDTORequest(
    @NotBlank(message = "MESSAGE_NOT_BLANK") String email, String firstName, String lastName)
    implements Serializable {}
